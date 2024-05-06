package nl.boonsboos.simeco.entities.bank;

import java.math.BigDecimal;

/**
 * A linear loan with interest amount decreasing over time
 */
public class Loan {
    private final long loanID;
    private final long bankID;
    private final long accountID;
    private BigDecimal remainingAmount;

    public Loan(long loanID, long bankID, long accountID, BigDecimal remainingAmount) {
        this.loanID = loanID;
        this.bankID = bankID;
        this.accountID = accountID;
        this.remainingAmount = remainingAmount;
    }

    public long getLoanID() {
        return loanID;
    }

    public long getBankID() {
        return bankID;
    }

    public long getAccountID() {
        return accountID;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}