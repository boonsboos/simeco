package nl.boonsboos.simeco.data.bank;

import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;

public class BankAccountDAO implements SimecoDAO<BankAccount> {
    @Override
    public BankAccount get(long id) {
        return null;
    }

    @Override
    public boolean save(BankAccount item) {
        return false;
    }
}
