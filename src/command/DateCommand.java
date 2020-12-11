package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Command that shows the user the current date
 * @author Kevin Zhao
 */

public final class DateCommand extends AbstractCommand {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    static {
       DATE_FORMAT.applyPattern("EEE, MMM d, ''yy h:mm a z");
    }

    public DateCommand() {
        super("date", "Shows user the current date");
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), String.format("Today's date and time is: %s", DATE_FORMAT.format(new Date()))));
    }

}