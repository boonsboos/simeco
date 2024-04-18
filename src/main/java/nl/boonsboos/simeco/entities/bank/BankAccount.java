package nl.boonsboos.simeco.entities.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * An account of a user at a bank.
 * An account number follows the following format: <code>[0-9]{6}[A-Z]{4,5}[0-9]{6}-[0-9]+</code>
 * @param accountID the ID of the account
 * @param userID the user's ID
 * @param bankID the bank the account is made
 * @param accountNumber the unique identifier that is part of the account
 * @param balance the balance of the account
 *
 */
public record BankAccount(
        // ignore because these don't need to be shown to the user
        @JsonIgnore long accountID,
        @JsonIgnore long userID,
        @JsonIgnore long bankID,
        String accountNumber,
        BigDecimal balance
) { }