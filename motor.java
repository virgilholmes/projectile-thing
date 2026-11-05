import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import java.util.concurrent.CountDownLatch;

/** Runs both L298N motor channels in one direction until Ctrl+C. */
public class motor implements AutoCloseable {
    private final Context pi;
    private final DigitalOutput in1;
    private final DigitalOutput in3;
    private boolean closed;

    public motor() {
        // BCM GPIO numbering: GPIO2 = physical pin 3, GPIO3 = physical pin 5.
        // Wire IN2/IN4 to GND, fit ENA/ENB jumpers, and share Pi/driver GND.
        // The driver needs its own suitable motor power supply.
        pi = Pi4J.newAutoContext();
        try {
            in1 = pi.create(DigitalOutput.newConfigBuilder(pi)
                    .id("motor-a").bcm(2)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW).build());
            in3 = pi.create(DigitalOutput.newConfigBuilder(pi)
                    .id("motor-b").bcm(3)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW).build());

        } catch (RuntimeException | Error e) {
            pi.shutdown();
            throw e;
        }
    }

    public synchronized void runFor(long milliseconds) throws InterruptedException {
        if (closed) {
            throw new IllegalStateException("Motor controller is closed");
        }
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Duration must be nonnegative");
        }
        try {
            in1.high();
            in3.high();
            Thread.sleep(milliseconds);
        } finally {
            try {
                in1.low();
            } finally {
                in3.low();
            }
        }
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            closed = true;
            pi.shutdown(); // Each output's shutdown state is LOW.
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try (var controller = new motor()) {
            Runtime.getRuntime().addShutdownHook(new Thread(controller::close, "motor-stop"));
            var in1 = controller.in1;
            var in3 = controller.in3;
            in1.high();
            in3.high();
            System.out.println("Motors running on GPIO2 and GPIO3. Press Ctrl+C to stop.");
            new CountDownLatch(1).await();
        }
    }
}
