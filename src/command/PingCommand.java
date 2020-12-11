package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;

/**
 * Command that shows the user their "ping"
 * @author Kevin Zhao
 */

public final class PingCommand extends AbstractCommand {

    public PingCommand() {
        super("ping", "Sends user the ping");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), "pong"));
    }

}
