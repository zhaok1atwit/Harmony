import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public final class ChatServer {

    public static void main(String[] args) throws Exception {
        final ServerSocket socket = new ServerSocket(1234);

        final Map<String, ClientWriteThread> users = Collections.synchronizedMap(new LinkedHashMap<>());

        while (true) {
            final Socket connection = socket.accept();
            if (connection != null) {
                final ClientWriteThread clientThread = new ClientWriteThread(connection, users);
                clientThread.start();
            }
        }

    }

}
