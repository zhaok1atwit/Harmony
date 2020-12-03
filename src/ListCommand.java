import java.io.IOException;

public final class ListCommand extends AbstractCommand {

    public ListCommand() {
        super("list", "Returns the currently connected users");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), String.format("Users: [%s]", String.join(", ", userManager.getAllUsers()))));
    }

}
