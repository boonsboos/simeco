package nl.boonsboos.simeco.util;

import nl.boonsboos.simeco.entities.bank.Bank;

public class AccountNumberGenerator {

    public static String newAccountNumber(Bank bank, long userID) {
        return  (int)((Math.random() * 200000) + 100000) +
                bank.bankInitials() +
                (int)((Math.random() * 200000) + 100000) +
                "-" + userID;
    }
}
