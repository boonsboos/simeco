package nl.boonsboos.simeco.entities.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction ledger entry
 * @param accountFrom the account id (not account number) of the account where the money originates from
 * @param accountTo the account id
 * @param amount
 * @param incoming
 * @param timestamp
 */
public record Transaction(long accountFrom, long accountTo, BigDecimal amount, boolean incoming, LocalDateTime timestamp) {
    public Transaction(long accountFrom, long accountTo, BigDecimal amount, boolean incoming) {
        this(accountFrom, accountTo, amount, incoming, LocalDateTime.now());
    }
}
