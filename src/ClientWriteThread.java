import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public final class ClientWriteThread extends Thread {
    public static final String QUIT_COMMAND = "{quit}";
    private static final String JOIN_MESSAGE = "%s has joined the chat room!\r\n";
    private static final String QUIT_MESSAGE = "%s has left the chat room!\r\n";
    private static final String NAME_CHOSEN_MESSAGE = "Welcome %s!\r\n";
    private final Socket socket;
    private final Map<String, ClientWriteThread> users;
    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private String name;

    public ClientWriteThread(Socket socket, Map<String, ClientWriteThread> users) {
        this.socket = socket;
        this.users = users;
        try {
            this.inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.name = null;
    }

    private String formatMessage(String message) {
        return String.format("%s: %s\r\n", name, message);
    }

    private void sendMessageToAllUsers(String message) {
        users.values().stream().filter(user -> !user.getSocket().isClosed()).forEach(user -> {
            try {
                user.getOutputStream().writeBytes(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (socket.isClosed()) {
                    return;
                }
                final String message = inputStream.readLine();
                if (message.startsWith(QUIT_COMMAND)) {
                    users.remove(name);
                    sendMessageToAllUsers(String.format(QUIT_MESSAGE, name));
                    break;
                } else if (name == null) {
                    name = message;
                    users.put(name, this);
                    outputStream.writeBytes(String.format(NAME_CHOSEN_MESSAGE, name));
                    sendMessageToAllUsers(String.format(JOIN_MESSAGE, name));
                } else {
                    final String formattedMessage = formatMessage(message);
                    sendMessageToAllUsers(formattedMessage);
                }
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
