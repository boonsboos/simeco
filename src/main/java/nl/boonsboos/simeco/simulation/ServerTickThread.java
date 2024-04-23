package nl.boonsboos.simeco.simulation;

import nl.boonsboos.simeco.data.special.ServerDAO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Schedules and handles the server ticking
 */
public class ServerTickThread {

    private static final Logger LOG = Logger.getLogger(ServerTickThread.class.getSimpleName());
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ServerDAO SERVER_DAO = new ServerDAO();

    private static LocalDateTime SERVER_TIME;

    public static final long TICK_DELAY = 60L;

    public static LocalDateTime getServerTime() {
        return SERVER_TIME;
    }

    public void start() {
        SERVER_TIME = SERVER_DAO.getServerTime();

        EXECUTOR_SERVICE.scheduleAtFixedRate(
            ServerTickThread::tick,
            TICK_DELAY,
            TICK_DELAY,
            TimeUnit.SECONDS
        );
    }

    public static void tick() {
        SERVER_TIME = SERVER_TIME.plusHours(1);

        // check if first day of the year
        if (SERVER_TIME.getDayOfMonth() == 1
            && SERVER_TIME.getMonth() == Month.JANUARY
            && SERVER_TIME.getHour() == 0
        ) {
            LOG.info("Happy new year! "+SERVER_TIME);
        }

        // check if first of quarter
        if (SERVER_TIME.getDayOfMonth() == 1
            && SERVER_TIME.getMonth() == SERVER_TIME.getMonth().firstMonthOfQuarter()
        ) {
            // tax time
            // dividends time
            // TODO: what else?
        }

        SERVER_DAO.saveServerTime(SERVER_TIME);
    }
}
