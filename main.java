import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebhookServer {

    public static void main(String[] args) throws IOException {
        // 1. Updated the port to 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        
        // Route 1: Serve the HTML button page
        server.createContext("/", new HomeHandler());
        
        // Route 2: Trigger the Java command
        server.createContext("/run-command", new CommandHandler());
        
        server.setExecutor(null); 
        server.start();
        System.out.println("Server running at http://localhost:8000");
    }

    // HTML Page with the button
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>java-button</title>
                    <style>
                      
                    </style>
                </head>
                <body>
                    <h2>java button print</h2>
                    <button onclick="runCommand()">press</button>
                    <div id="status"></div>

                    <script>
                        function runCommand() {
                            document.getElementById('status').innerText = "Running...";
                            fetch('/run-command', { method: 'POST' })
                                .then(response => response.text())
                                .then(text => { document.getElementById('status').innerText = text; })
                                .catch(err => { document.getElementById('status').innerText = "Error triggering command"; });
                        }
                    </script>
                </body>
                </html>
                """;
            
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // Code that runs the command and prints to the terminal
    static class CommandHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); 
                return;
            }

            String responseMessageBody;
            try {
                // 2. Prints a line directly to the server's terminal console
                System.out.println("[BUTTON PRESS]");

                // 3. Executes a Java command that prints to the standard system output
                // This targets the system's shell to execute a string command
                ProcessBuilder pb = new ProcessBuilder(
                    "bash", "-c", "echo 'Hello from the triggered Java process!'"
                );
                
                // Inherit IO so the sub-process prints directly to your active terminal windows
                pb.inheritIO(); 
                Process process = pb.start();
                
                int exitCode = process.waitFor();
                responseMessageBody = "Success! Command finished with exit code: " + exitCode;
                
            } catch (Exception e) {
                responseMessageBody = "Failed to run command: " + e.getMessage();
                System.err.println("[ERROR] " + e.getMessage());
            }

            byte[] bytes = responseMessageBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
