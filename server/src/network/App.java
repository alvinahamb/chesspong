package network;
public class App {
    public static void main(String[] args) throws Exception {
        int port = 5000;
        try (Server server = new Server(port)) {
            server.start();
            System.out.println("Server started on port " + port + ". Press Enter to stop.");
            System.in.read();
        }
        System.out.println("Server stopped.");
    }
}
