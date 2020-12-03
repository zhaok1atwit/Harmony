import java.io.IOException;

public final class PingCommand extends AbstractCommand {

    public PingCommand() {
        super("ping", "Sends user the ping");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), "pong"));
    }

}
