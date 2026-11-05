import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


public class main {
    private static motor motors;

    public static void main(String[] args) throws Exception {
        motors = new motor();
        Runtime.getRuntime().addShutdownHook(new Thread(motors::close, "motor-stop"));
        HttpServer server = HttpServer.create(
            new InetSocketAddress("localhost", 8080), 0
        );
        // creates a shoot page that calls shoot() on a POST request
        server.createContext("/shoot", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            shoot();

            byte[] response = "test from console".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);

            try (var output = exchange.getResponseBody()) {
                output.write(response);
            }
        });
        server.createContext("/move", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            move();
        });
        server.start();
        System.out.println("Java backend http server running on :8080");
    }

    // what ever is in this class is what is called when the shoot button is clicked
    public static void shoot() {
        try {
            motors.runFor(200); // Run both motors for 200 milliseconds.
        } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
        }
        System.out.println("Trigger");
    }
    public static void move() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
        }
        System.out.println("Move!");
    }
}
