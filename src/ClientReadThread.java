import java.io.BufferedReader;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ClientReadThread extends Thread {
    private final Socket socket;
    private final BufferedReader inputStream;
    private final List<String> messages;

    public ClientReadThread(Socket socket, BufferedReader inputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.messages = Collections.synchronizedList(new LinkedList<>());
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void run() {
        while (true) {
            if (socket.isClosed()) {
                return;
            }

            try {
                final String incoming = inputStream.readLine();
                if (incoming == null) {
                    continue;
                }
                messages.add(incoming);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
