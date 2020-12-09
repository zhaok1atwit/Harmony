package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;

/**
 * Command that will disconnect the client from the chat room (note: this command is sent internally when the client's GUI is closed)
 * @author Matt Lefebvre
 */
public final class QuitCommand extends AbstractCommand {

    public QuitCommand() {
        super("quit", "Exits the chatroom");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        try {
            userManager.removeUser(clientWriteThread.getName());
            userManager.broadcast(new Message(message.getColor(), message.getFont(), String.format("%s has left the chat room!", clientWriteThread.getUserName())));
            clientWriteThread.getInputStream().close();
            clientWriteThread.getOutputStream().close();
            clientWriteThread.getSocket().close();
        } catch (Exception e) {
        }
    }

}
