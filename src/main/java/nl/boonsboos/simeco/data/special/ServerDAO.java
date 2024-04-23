package nl.boonsboos.simeco.data.special;

import nl.boonsboos.simeco.data.DatabasePool;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * <b>Does not implement SimecoDAO!</b>
 * Only has queries that are useful for the {@link Server}.
 */
public class ServerDAO {

    private static final Logger LOG = Logger.getLogger(ServerDAO.class.getSimpleName());

    public LocalDateTime getServerTime() {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement fetchServerTime = conn.prepareStatement(
                "SELECT * FROM servertime;"
            );

            ResultSet result = fetchServerTime.executeQuery();

            if (result.next()) {
                return result.getTimestamp("time").toLocalDateTime();
            }
        } catch (SQLException e) {
            LOG.warning("Failed to fetch server time: " + e.getMessage());
        }

        return null;
    }

    public boolean saveServerTime(LocalDateTime time) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement updateServerTime = conn.prepareStatement(
                "UPDATE servertime SET time = ?;"
            );

            updateServerTime.setTimestamp(1, Timestamp.valueOf(time));

            updateServerTime.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to fetch server time: " + e.getMessage());
        }

        return false;
    }

}
