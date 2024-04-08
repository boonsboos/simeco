package nl.boonsboos.simeco.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.boonsboos.simeco.Simeco;
import nl.boonsboos.simeco.util.SimecoConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabasePool {

    private static final Logger LOG = Logger.getLogger("DatabasePool");
    private static HikariDataSource dataSource;

    /**
     * Initializes the connection pool, opening connections to the configured database.
     */
    public static void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Simeco.CONFIG.getDatabaseURL()+"/"+Simeco.CONFIG.getDatabaseName()+"?ssl=false");
        config.setUsername(Simeco.CONFIG.getDatabaseUser());
        config.setPassword(Simeco.CONFIG.getDatabasePassword());
        config.setPoolName("SimecoPool-1");
        config.setMaximumPoolSize(50);

        dataSource = new HikariDataSource(config);
    }

    /**
     * Gets a connection from the connection pool
     * @return a connection from the pool.
     */
    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.severe("Failed to take connection");
            throw new RuntimeException();
        }
    }
}
