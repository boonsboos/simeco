package nl.boonsboos.simeco.entities.bank;

import java.math.BigDecimal;

/**
 * An account of a user at a bank
 * @param userID
 * @param bankID
 * @param accountNumber
 * @param balance
 */
public record BankAccount(
        long accountID,
        long userID,
        long bankID,
        String accountNumber,
        BigDecimal balance
) { }