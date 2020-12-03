import java.io.*;
import java.net.Socket;
import java.util.Map;

public final class ClientWriteThread extends Thread {
    public static final String SERVER_COLOR = "BLACK";
    public static final String SERVER_FONT = "ARIAL";
    private static final String JOIN_MESSAGE = "%s has joined the chat room!\r\n";
    private static final String NAME_CHOSEN_MESSAGE = "Welcome %s!\r\n";
    private final Socket socket;
    private final UserManager userManager;
    private final Map<String, AbstractCommand> commands;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String userName;

    public ClientWriteThread(Socket socket, UserManager userManager, Map<String, AbstractCommand> commands) {
        this.socket = socket;
        this.userManager = userManager;
        this.commands = commands;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.userName = null;
    }

    private String formatMessage(String message) {
        return String.format("%s: %s\r\n", userName, message);
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

    public String getUserName() {
        return userName;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (socket.isClosed()) {
                    return;
                }

                final Message message = (Message) inputStream.readObject();
                final String content = message.getContent();

                if (userName == null) {
                    userName = message.getContent();
                    userManager.addUser(userName, this);
                    outputStream.writeBytes(String.format(NAME_CHOSEN_MESSAGE, userName));
                    userManager.broadcast(new Message(SERVER_COLOR, SERVER_FONT, String.format(JOIN_MESSAGE, userName)));
                    continue;
                }

                if (content.startsWith("/")) {
                    final String[] splitContent = content.split(" ");
                    final AbstractCommand abstractCommand = commands.get(splitContent[0].substring(1));
                    if (abstractCommand != null) {
                        final int adjustedLength = splitContent.length - 1;
                        final String[] commandArgs = new String[adjustedLength];
                        System.arraycopy(splitContent, 1, commandArgs, 0, adjustedLength);
                        abstractCommand.perform(userManager, message,this, commandArgs);
                    }
                    continue;
                }

                userManager.broadcast(new Message(message.getColor(), message.getFont(), formatMessage(message.getContent())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
