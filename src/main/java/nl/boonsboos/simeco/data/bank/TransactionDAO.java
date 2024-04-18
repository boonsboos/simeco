package nl.boonsboos.simeco.data.bank;

import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.Transaction;

public class TransactionDAO implements SimecoDAO<Transaction> {

    @Override
    public Transaction get(long id) {
        return null;
    }

    @Override
    public boolean save(Transaction item) {
        return false;
    }
}
