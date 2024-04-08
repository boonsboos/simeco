package nl.boonsboos.simeco.auth;

import nl.boonsboos.simeco.auth.data.AuthClientDAO;
import nl.boonsboos.simeco.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores clients and their information.
 * Should only be used as a cache, not as a permanent store.
 */
public class ClientStore {

    private static final List<AuthClient> clients = new ArrayList<>();
    private static final AuthClientDAO AUTH_CLIENT_DAO = new AuthClientDAO();

    /**
     * Creates and saves the client of the user.
     * @param user the user to create a new client for
     */
    public static void saveNewClient(User user) {

        AuthClient client = new AuthClient(
            user.getUserID(),
            SecretGenerator.generateSecret()
        );

        AUTH_CLIENT_DAO.save(client);
    }

    /**
     * Gets auth client by their user
     * @param user the user to get the auth client for
     * @return the client if found, null otherwise
     */
    public static AuthClient getClientByUser(User user) {
        List<AuthClient> collected = clients.stream().filter(
            client -> client.userID() == user.getUserID()
        ).toList();

        return collected.isEmpty() ? AUTH_CLIENT_DAO.get(user.getUserID()) : collected.getFirst();
    }

    /**
     * Gets an auth client by their key or secret. Used to verify the key of a user.
     * @param key the key or secret the user sent
     * @return the client if found, null otherwise
     */
    public static AuthClient getClientByKey(String key) {
        List<AuthClient> collected = clients.stream().filter(
                client -> client.clientSecret().equals(key)
        ).toList();

        return collected.isEmpty() ? AUTH_CLIENT_DAO.verifySecret(key) : collected.getFirst();
    }
}
