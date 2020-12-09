package struct;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Stores all the users connected to the chat room in addition to providing means of broadcasting messages to all connected users
 * @author Matt Lefebvre
 */
public class UserManager {
    private final Map<String, ClientWriteThread> users;

    /**
     * UserManager constructor
     * @param users users
     */
    public UserManager(Map<String, ClientWriteThread> users) {
        this.users = users;
    }

    /**
     * Adds a user to the UserManager
     * @param name name of the user
     * @param clientWriteThread the ClientWriteThread associated with the user
     * @return the previous ClientWriteThread associated with the given username or null if not present
     */
    public ClientWriteThread addUser(String name, ClientWriteThread clientWriteThread) {
        return this.users.put(name, clientWriteThread);
    }

    /**
     * Retrieves a user from the UserManager
     * @param name name
     * @return the ClientWriteThread associated with the name or null if not present
     */
    public ClientWriteThread getUser(String name) {
        return this.users.get(name);
    }

    /**
     * Removes a user from the UserManager
     * @param name name
     * @return the ClientWriteThread associated with the name or none if it was not able to be removed (i.e - not present)
     */
    public ClientWriteThread removeUser(String name) {
        return this.users.remove(name);
    }

    /**
     * Returns the names of all connected users
     * @return names
     */
    public Set<String> getAllUsers() {
        return this.users.keySet();
    }

    /**
     * Broadcasts a message to the chat room (i.e. - sends the message to every user)
     * @param message message
     */
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
