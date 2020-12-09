package struct;

import command.AbstractCommand;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Thread that processes messages sent from the client to the server. The first message sent to the server from the client
 * contains the clients username. After the username has been set, all following messages will execute commands or broadcast
 * the message to the chat room depending on what was sent
 * @author Matt Lefebvre
 */
public final class ClientWriteThread extends Thread {
    public static final String SERVER_COLOR = "BLACK";
    public static final String SERVER_FONT = "ARIAL";
    private final Socket socket;
    private final UserManager userManager;
    private final Map<String, AbstractCommand> commands;
    private final long joinTime;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String userName;

    /**
     * ClientWriteThread constructor
     * @param socket socket
     * @param userManager usermanager
     * @param commands commands
     */
    public ClientWriteThread(Socket socket, UserManager userManager, Map<String, AbstractCommand> commands) {
        this.socket = socket;
        this.userManager = userManager;
        this.commands = commands;
        this.joinTime = System.currentTimeMillis();
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

    public long getJoinTime() {
        return joinTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (socket.isClosed()) {
                    return;
                }

                // Read the message from the client and extract the actual content of the message
                final Message message = (Message) inputStream.readObject();
                final String content = message.getContent();

                // Check if the username needs to be set
                if (userName == null) {
                    userName = message.getContent();
                    userManager.addUser(userName, this);
                    outputStream.writeBytes(String.format("Welcome %s!", userName));
                    userManager.broadcast(new Message(SERVER_COLOR, SERVER_FONT, String.format("%s has joined the chat room!", userName)));
                    continue;
                }

                // Check if the message is a command
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

                // Username has already been set and this message is NOT a command, so broadcast its contents to the chat room
                userManager.broadcast(new Message(message.getColor(), message.getFont(), formatMessage(message.getContent())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
