import java.io.*;
import java.net.Socket;
import java.util.Map;

public final class ClientWriteThread extends Thread {
    public static final String QUIT_COMMAND = "{quit}";
    private static final String SERVER_COLOR = "BLACK";
    private static final String SERVER_FONT = "ARIAL";
    private static final String JOIN_MESSAGE = "%s has joined the chat room!\r\n";
    private static final String QUIT_MESSAGE = "%s has left the chat room!\r\n";
    private static final String NAME_CHOSEN_MESSAGE = "Welcome %s!\r\n";
    private final Socket socket;
    private final Map<String, ClientWriteThread> users;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String name;

    public ClientWriteThread(Socket socket, Map<String, ClientWriteThread> users) {
        this.socket = socket;
        this.users = users;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.name = null;
    }

    private String formatMessage(String message) {
        return String.format("%s: %s\r\n", name, message);
    }

    private void sendMessageToAllUsers(Message message) {
        users.values().forEach(user -> {
            try {
                user.getOutputStream().writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (socket.isClosed()) {
                    return;
                }

                final Message message = (Message) inputStream.readObject();
                if (message.getContent().startsWith(QUIT_COMMAND)) {
                    users.remove(name);
                    sendMessageToAllUsers(new Message(SERVER_COLOR, SERVER_FONT, String.format(QUIT_MESSAGE, name)));
                    break;
                } else if (name == null) {
                    name = message.getContent();
                    users.put(name, this);
                    outputStream.writeBytes(String.format(NAME_CHOSEN_MESSAGE, name));
                    sendMessageToAllUsers(new Message(SERVER_COLOR, SERVER_FONT, String.format(JOIN_MESSAGE, name)));
                } else {
                    sendMessageToAllUsers(new Message(message.getColor(), message.getFont(), formatMessage(message.getContent())));
                }
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
