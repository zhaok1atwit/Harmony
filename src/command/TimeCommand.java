package command;

import struct.ClientWriteThread;
import struct.Message;
import struct.UserManager;

import java.io.IOException;

public final class TimeCommand extends AbstractCommand {

    public TimeCommand() {
        super("time", "How long user has been online for");
    }

    private String formatSecondsAsTime(int seconds) {
        int d, h, m;

        d = seconds / 86400;
        seconds -= d * 86400;

        h = seconds / 3600;
        seconds -= h * 3600;

        m = seconds / 60;
        seconds -= m * 60;

        if (d > 0) {
            return String.format("%dd, %dh, %dm, %ds", d, h, m, seconds);
        } else if (h > 0) {
            return String.format("%dh, %dm, %ds", h, m, seconds);
        } else if (m > 0) {
            return String.format("%dm, %ds", m, seconds);
        } else {
            return seconds + "s";
        }
    }

    @Override
    public void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException {
        final int secondsOnline = (int) ((System.currentTimeMillis() - clientWriteThread.getJoinTime()) / 1000);
        clientWriteThread.getOutputStream().writeObject(new Message(message.getColor(), message.getFont(), String.format("You've been online for: %s", formatSecondsAsTime(secondsOnline))));
    }

}
