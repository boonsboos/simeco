package nl.boonsboos.simeco.data.bank;

import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.Bank;

public class BankDAO implements SimecoDAO<Bank> {
    @Override
    public Bank get(long id) {
        return null;
    }

    @Override
    public boolean save(Bank item) {
        return false;
    }
}
