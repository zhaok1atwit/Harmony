import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public final class ChatServer {

    public static void main(String[] args) throws Exception {
        final ServerSocket socket = new ServerSocket(1234);

        final UserManager userManager = new UserManager(Collections.synchronizedMap(new LinkedHashMap<>()));

        final Map<String, AbstractCommand> commands = new HashMap<>();
        final AbstractCommand[] allCommands = {
                new ListCommand(),
                new MsgCommand(),
                new QuitCommand(),
                new TimeCommand(),
                new PingCommand(),
                new DateCommand()
        };
        for (AbstractCommand abstractCommand : allCommands) {
            commands.put(abstractCommand.getAlias(), abstractCommand);
        }

        while (true) {
            final Socket connection = socket.accept();
            if (connection != null) {
                final ClientWriteThread clientThread = new ClientWriteThread(connection, userManager, commands);
                clientThread.start();
            }
        }

    }

}
