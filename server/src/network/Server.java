package network;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Closeable {
    private final int port;
    private final ExecutorService executor;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        if (running) {
            return;
        }
        serverSocket = new ServerSocket(port);
        running = true;
        executor.execute(this::acceptLoop);
        System.out.println("Listening on port " + port);
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                executor.execute(() -> handleClient(socket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        System.out.println("Client connected: " + socket.getRemoteSocketAddress());
        try (Socket closeable = socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(closeable.getInputStream()));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(closeable.getOutputStream()), true)) {

            writer.println("WELCOME");
            String line;
            while (running && (line = reader.readLine()) != null) {
                System.out.println("Client says: " + line);
                writer.println("ECHO " + line);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }
        System.out.println("Client disconnected.");
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        executor.shutdownNow();
    }

    @Override
    public void close() {
        stop();
    }
}
