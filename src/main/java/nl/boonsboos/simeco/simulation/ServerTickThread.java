package nl.boonsboos.simeco.simulation;

import nl.boonsboos.simeco.data.entities.bank.BankAccountDAO;
import nl.boonsboos.simeco.data.entities.bank.BankDAO;
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

    public static final long TICK_DELAY = 60L;
    private static final Logger LOG = Logger.getLogger(ServerTickThread.class.getSimpleName());
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final ServerDAO SERVER_DAO = new ServerDAO();

    private static LocalDateTime SERVER_TIME;

    // DAOs
    private static final BankAccountDAO BANK_ACCOUNT_DAO = new BankAccountDAO();

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

    private static void tick() {
        SERVER_TIME = SERVER_TIME.plusHours(1);

        // actions that occur on the first day of the new year
        if (SERVER_TIME.getDayOfMonth() == 1
            && SERVER_TIME.getMonth() == Month.JANUARY
            && SERVER_TIME.getHour() == 0
        ) {
            LOG.info("Happy new year! "+SERVER_TIME);

            // savings interest (actual interest is too hard for now)
            BANK_ACCOUNT_DAO.calculateAndApplyDepositInterestGlobally();

            // adjust prices to account for inflation
            // government publishes news about inflation numbers and new policies that go into action
        }

        // actions that occur on the first day of the new quarter
        if (SERVER_TIME.getDayOfMonth() == 1
            && SERVER_TIME.getMonth() == SERVER_TIME.getMonth().firstMonthOfQuarter()
        ) {
            // tax return time
            // dividends time
            // companies publish their quarterly reports
        }

        // actions that occur on the first day of the month
        if (SERVER_TIME.getDayOfMonth() == 1) {
            // pay loans
            // pay bills
        }

        /*
         * The following actions occur every hour in server time
         */

        // tick all volatile equity (stocks, options, etc.)

        SERVER_DAO.saveServerTime(SERVER_TIME);
    }
}
