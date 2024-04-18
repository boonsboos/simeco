package nl.boonsboos.simeco.controllers.responses.bank;

/**
 * A response to a bank transfer. May contain an error message.
 * @param from the account number of the account from which the currency was transferred
 * @param to the account number of the account receiving the currency
 * @param message a message, may be an error or a confirmation
 */
public record BankTransferResponse(String from, String to, String message) {
    public static String TRANSACTION_FAILED = "Transaction failed. Try again later.";
    public static String TRANSACTION_SUCCESS = "Transaction completed successfully.";
    public static String INSUFFICIENT_BALANCE = "Insufficient balance to complete transaction.";
}
