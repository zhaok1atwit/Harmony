import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class UserManager {
    private final Map<String, ClientWriteThread> users;

    public UserManager(Map<String, ClientWriteThread> users) {
        this.users = users;
    }

    public ClientWriteThread addUser(String name, ClientWriteThread clientWriteThread) {
        return this.users.put(name, clientWriteThread);
    }

    public ClientWriteThread getUser(String name) {
        return this.users.get(name);
    }

    public ClientWriteThread removeUser(String name) {
        return this.users.remove(name);
    }

    public Set<String> getAllUsers() {
        return this.users.keySet();
    }

    public void broadcast(Message message) {
        this.users.values().removeIf(user -> user.getSocket().isClosed());
        if (users.values().isEmpty()) {
            return;
        }
        this.users.values().forEach(user -> {
            try {
                user.getOutputStream().writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
