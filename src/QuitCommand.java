import java.io.IOException;

public final class QuitCommand extends AbstractCommand {
    private static final String QUIT_MESSAGE = "%s has left the chat room!\r\n";

    public QuitCommand() {
        super("quit", "Exits the chatroom");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        try {
            userManager.removeUser(clientWriteThread.getName());
            userManager.broadcast(new Message(message.getColor(), message.getFont(), String.format(QUIT_MESSAGE, clientWriteThread.getUserName())));
            clientWriteThread.getInputStream().close();
            clientWriteThread.getOutputStream().close();
            clientWriteThread.getSocket().close();
        } catch (Exception e) {
        }
    }

}
