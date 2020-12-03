import java.io.IOException;

public abstract class AbstractCommand {
    private final String alias;
    private final String description;

    public AbstractCommand(String alias, String description) {
        this.alias = alias;
        this.description = description;
    }

    public final String getAlias() {
        return alias;
    }

    public abstract void perform(UserManager userManager, Message message, ClientWriteThread clientWriteThread, String[] args) throws IOException;

}
