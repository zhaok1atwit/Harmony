package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;

/**
 * Command that will privately message a user if they are present in the chat room or send an error message if provided invalid arguments or an invalid target
 * @author Matt Lefebvre
 */
public final class MsgCommand extends AbstractCommand {

    public MsgCommand() {
        super("msg", "Sends a message privately to a user (/msg <user> <msg>");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        if (args.length < 2) {
            clientWriteThread.getOutputStream().writeObject(new Message("", "", String.format("Incorrect number of args %d, need 2", args.length)));
            return;
        }

        final ClientWriteThread user = userManager.getUser(args[0]);
        if (user == null) {
            clientWriteThread.getOutputStream().writeObject(new Message("", "", String.format("%s was not found!", args[0])));
            return;
        }

        final StringBuilder builder = new StringBuilder();
        for (int n = 1; n < args.length; n++) {
            builder.append(args[n]).append(" ");
        }
        final String msgToSend = builder.toString();
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), String.format("You -> %s: %s", user.getUserName(), msgToSend)));
        user.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), String.format("%s -> You: %s", clientWriteThread.getUserName(), msgToSend)));
    }

}
