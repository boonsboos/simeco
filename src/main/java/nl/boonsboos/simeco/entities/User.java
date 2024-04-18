package nl.boonsboos.simeco.entities;

/**
 * Represents a player participating in Simeco.
 * Make sure that endpoints do not leak information that does not belong to this user.
 *
 * @param userID the ID of the user
 * @param username the user's GitHub username
 */
public record User( long userID, String username) {

    /**
     * If the user has not yet been saved, use this.
     * @param username the user's name
     */
    public User(String username) {
        this(-1, username);
    }
}
