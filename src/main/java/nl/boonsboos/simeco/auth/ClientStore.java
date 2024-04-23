package nl.boonsboos.simeco.auth;

import nl.boonsboos.simeco.auth.data.AuthClientDAO;
import nl.boonsboos.simeco.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Stores clients and their information.
 * Should only be used as a cache, not as a permanent store.
 */
public class ClientStore {

    private static final Logger LOG = Logger.getLogger(ClientStore.class.getSimpleName());

    private static boolean initialized;
    private static final List<AuthClient> clients = new ArrayList<>();
    private static final AuthClientDAO AUTH_CLIENT_DAO = new AuthClientDAO();

    /**
     * Loads most recent clients into the client store
     */
    public static void intialize() {

        LOG.info("Initializing client store");

        if (!initialized) {
            clients.addAll(AUTH_CLIENT_DAO.getAll());
            initialized = true;
        }
    }

    /**
     * Creates and saves the client of the user.
     * @param user the user to create a new client for
     */
    public static void saveNewClient(User user) {

        AuthClient client = new AuthClient(
            user.userID(),
            SecretGenerator.generateSecret()
        );

        LOG.info("New player! "+user);

        AUTH_CLIENT_DAO.save(client);
    }

    /**
     * Gets auth client by their user
     * @param user the user to get the auth client for
     * @return the client if found, null otherwise
     */
    public static AuthClient getClientByUser(User user) {
        List<AuthClient> collected = clients.stream().filter(
            client -> client.userID() == user.userID()
        ).toList();

        return collected.isEmpty() ? AUTH_CLIENT_DAO.get(user.userID()) : collected.getFirst();
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

        if (collected.isEmpty()) {
            AuthClient c = AUTH_CLIENT_DAO.verifySecret(key);
            clients.add(c);
            return c;
        } else {
            return collected.getFirst();
        }
    }
}
