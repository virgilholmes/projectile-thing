import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/** Runs both L298N motor channels in one direction until Ctrl+C. */
public class motor {
    public static void main(String[] args) throws InterruptedException {
        // BCM GPIO numbering: GPIO2 = physical pin 3, GPIO3 = physical pin 5.
        // Wire IN2/IN4 to GND, fit ENA/ENB jumpers, and share Pi/driver GND.
        // The driver needs its own suitable motor power supply.
        var pi = Pi4J.newAutoContext();
        var stopped = new AtomicBoolean();
        Runnable stop = () -> {
            if (stopped.compareAndSet(false, true)) {
                pi.shutdown(); // Each output's shutdown state is LOW.
            }
        };

        try {
            var in1 = pi.create(DigitalOutput.newConfigBuilder(pi)
                    .id("motor-a").bcm(2)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW).build());
            var in3 = pi.create(DigitalOutput.newConfigBuilder(pi)
                    .id("motor-b").bcm(3)
                    .initial(DigitalState.LOW)
                    .shutdown(DigitalState.LOW).build());

            Runtime.getRuntime().addShutdownHook(new Thread(stop, "motor-stop"));
            in1.high();
            in3.high();
            System.out.println("Motors running on GPIO2 and GPIO3. Press Ctrl+C to stop.");
            new CountDownLatch(1).await();
        } finally {
            stop.run();
        }
    }
}
