package nl.boonsboos.simeco.auth.data;

import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AuthClientDAO implements SimecoDAO<AuthClient> {

    private static final Logger LOG = Logger.getLogger(AuthClientDAO.class.getSimpleName());

    @Override
    public AuthClient get(long id) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getAuthClient = conn.prepareStatement(
                "SELECT * FROM authuser "+
                "WHERE userid = ?;"
            );

            getAuthClient.setLong(1, id);

            ResultSet result = getAuthClient.executeQuery();

            if (result.next()) {
                return new AuthClient(
                    result.getLong("userid"),
                    result.getString("secret")
                );
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get auth client by ID: "+e.getMessage());
        }

        return null;
    }

    public List<AuthClient> getAll() {
        List<AuthClient> list = new ArrayList<>();

        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getAllAuthClients = conn.prepareStatement(
                "SELECT * FROM authuser;"
            );

            ResultSet result = getAllAuthClients.executeQuery();

            while (result.next()) {
                list.add(new AuthClient(
                    result.getLong("userid"),
                    result.getString("secret")
                ));
            }

        } catch (SQLException e) {
            LOG.warning("Failed to get all auth clients in time period:"+e.getMessage());
        }

        return list;
    }

    @Override
    public boolean save(AuthClient item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement saveAuthUser = conn.prepareStatement(
                "INSERT INTO authuser (userid, secret) "+
                "VALUES (?, ?) ON CONFLICT DO NOTHING;"
            );

            saveAuthUser.setLong(1, item.userID());
            saveAuthUser.setString(2, item.clientSecret());

            saveAuthUser.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to save auth client: "+e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the secret exists and gets the corresponding client
     * @param secret the secret submitted.
     * @return the client whose secret to check
     */
    public AuthClient verifySecret(String secret) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getAuthClient = conn.prepareStatement(
                    "SELECT * FROM authuser "+
                    "WHERE secret = ?;"
            );

            getAuthClient.setString(1, secret);

            ResultSet result = getAuthClient.executeQuery();

            if (result.next()) {
                return new AuthClient(
                    result.getLong("userid"),
                    result.getString("secret")
                );
            }
        } catch (SQLException e) {
            LOG.warning("Failed to verify client secret: "+e.getMessage());
        }

        return null;
    }
}
