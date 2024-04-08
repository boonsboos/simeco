package nl.boonsboos.simeco.data;

import nl.boonsboos.simeco.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * A DAO for user queries for use by user endpoints.
 * @see nl.boonsboos.simeco.controllers.UserController
 */
public class UserDAO implements SimecoDAO<User> {

    private static final Logger LOG = Logger.getLogger(UserDAO.class.getSimpleName());

    @Override
    public User get(long id) {
        try (Connection conn = DatabasePool.getConnection()){
            PreparedStatement getUserByID = conn.prepareStatement(
                "SELECT * FROM seuser\n"+
                "WHERE userid = ?;"
            );

            getUserByID.setLong(1, id);

            ResultSet result = getUserByID.executeQuery();

            if (result.next()) {
                return new User(
                    result.getInt("userid"),
                    result.getString("username")
                );
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get user by ID: "+e.getMessage());
        }

        return null;
    }

    public User get(String username) {
        try (Connection conn = DatabasePool.getConnection()){
            PreparedStatement getUserByID = conn.prepareStatement(
                "SELECT * FROM seuser "+
                "WHERE username = ?;"
            );

            getUserByID.setString(1, username);

            ResultSet result = getUserByID.executeQuery();

            if (result.next()) {
                return new User(
                    result.getInt("userid"),
                    result.getString("username")
                );
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get user by name: "+e.getMessage());
        }

        return null;
    }

    @Override
    public boolean save(User item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO seuser (username) "+
                "VALUES (?) ON CONFLICT DO NOTHING;"
            );

            statement.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to save new user: "+e.getMessage());

            return false;
        }
    }
}
