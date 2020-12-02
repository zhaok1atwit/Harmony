import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ClientReadThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final List<Message> messages;

    public ClientReadThread(Socket socket, ObjectInputStream inputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.messages = Collections.synchronizedList(new LinkedList<>());
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public void run() {
        while (true) {
            if (socket.isClosed()) {
                return;
            }

            try {
                inputStream.read();
                final Message incoming = (Message) inputStream.readObject();
                if (incoming == null) {
                    continue;
                }
                messages.add(incoming);
            } catch (Exception e) {
            }
        }
    }

}
