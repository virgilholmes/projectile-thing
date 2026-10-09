import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class main {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("localhost", 8080), 0
        );

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

        server.start();
        System.out.println("Java backend http server running on :8080");
    }

    // what ever is in this class is what is called when the shoot button is clicked
    public static void shoot() {
        System.out.println("The button has been clicked");
    }
}
