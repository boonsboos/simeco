package nl.boonsboos.simeco.auth;

/**
 * Represents the authentication side of the user
 * @param userID the id of the user
 * @param clientSecret the secret of the client
 */
public record AuthClient(
    long userID,
    String clientSecret
) { }
