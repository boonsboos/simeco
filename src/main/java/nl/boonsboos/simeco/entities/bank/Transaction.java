package nl.boonsboos.simeco.entities.bank;

import nl.boonsboos.simeco.simulation.ServerTickThread;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction ledger entry
 * @param accountFrom the account id (not account number) of the sender
 * @param accountTo the account id of the receiver
 * @param amount the amount of currency sent
 * @param timestamp the time at which the transaction was completed
 */
public record Transaction(
        long accountFrom,
        long accountTo,
        BigDecimal amount,
        LocalDateTime timestamp
) {
    public Transaction(long accountFrom, long accountTo, BigDecimal amount) {
        this(accountFrom, accountTo, amount, ServerTickThread.getServerTime());
    }
}
