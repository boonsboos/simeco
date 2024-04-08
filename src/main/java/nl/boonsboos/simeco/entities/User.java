package nl.boonsboos.simeco.entities;

/**
 * Represents a player participating in Simeco
 */
public class User {
    /**
     * The ID of the user.
     * Make sure that endpoints do not leak information that does not belong to this user.
     */
    private final long userID;
    /**
     * The user's GitHub username
     */
    private final String username;

    public User(long userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public User(String username) {
        this.userID = -1;
        this.username = username;
    }

    public long getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
}
