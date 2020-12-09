package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;

/**
 * AbstractCommand class for commands that are implemented into the chatroom
 * @author Matt Lefebvre
 */
public abstract class AbstractCommand {
    private final String alias;
    private final String description;

    /**
     * AbstractCommand constructor
     * @param alias alias of the command (e.g. - list, do not include the /)
     * @param description description of the command (e.g. - lists online users)
     */
    public AbstractCommand(String alias, String description) {
        this.alias = alias;
        this.description = description;
    }

    /**
     * Getter method for the command's alias
     * @return alias
     */
    public final String getAlias() {
        return alias;
    }

    /**
     * Getter method for the command's description
     * @return description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Performs the command
     * @param userManager the UserManager
     * @param message the original Message
     * @param clientWriteThread the ClientWriteThread that executed this command
     * @param args command arguments
     * @throws IOException exception
     */
    public abstract void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException;

}
