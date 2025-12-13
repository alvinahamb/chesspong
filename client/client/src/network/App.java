package network;
public class App {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 5000;

        try (ClientConnection conn = new ClientConnection(host, port)) {
            conn.connect();
            System.out.println("Connected to server " + host + ":" + port);

            String welcome = conn.readLine();
            System.out.println("Server -> " + welcome);

            conn.sendLine("PING");
            String echo = conn.readLine();
            System.out.println("Server -> " + echo);
        }

        System.out.println("Client finished.");
    }
}
