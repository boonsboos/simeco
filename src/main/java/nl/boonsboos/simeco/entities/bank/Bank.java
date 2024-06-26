package nl.boonsboos.simeco.entities.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * A bank
 * @param bankID the bank's ID in the database
 * @param bankName the name of the bank
 * @param bankInitials the initials of the bank
 * @param depositInterest interest given for deposits
 * @param loanInterest the interest asked for loans
 * @param vaultBalance the amount of money in the vault
 */
public record Bank(
        @JsonIgnore long bankID,
        String bankName,
        String bankInitials,
        float depositInterest,
        float loanInterest,
        @JsonIgnore BigDecimal vaultBalance
) { }