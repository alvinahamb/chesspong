package network;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Closeable {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        if (socket != null && socket.isConnected()) {
            return;
        }
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public String readLine() throws IOException {
        if (reader == null) {
            throw new IllegalStateException("Not connected");
        }
        return reader.readLine();
    }

    public void sendLine(String message) {
        if (writer == null) {
            throw new IllegalStateException("Not connected");
        }
        writer.println(message);
    }

    @Override
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {
        }
        if (writer != null) {
            writer.close();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
