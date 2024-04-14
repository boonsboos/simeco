package nl.boonsboos.simeco.data.bank;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class BankAccountDAO implements SimecoDAO<BankAccount> {

    private static final Logger LOG = Logger.getLogger(BankAccountDAO.class.getSimpleName());

    /**
     * Gets the bank account by its ID
     * @param id the id of the item
     * @return the account if found, null otherwise
     */
    @Override
    public BankAccount get(long id) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getBankAccount = conn.prepareStatement(
                "SELECT * FROM bankaccounts "+
                "WHERE accountid = ?;"
            );

            getBankAccount.setLong(1, id);

            ResultSet result = getBankAccount.executeQuery();

            if (result.next()) {
                return new BankAccount(
                    result.getLong("accountid"),
                    result.getLong("userid"),
                    result.getLong("bankid"),
                    result.getString("accountnumber"),
                    result.getBigDecimal("balance")
                );
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get bank account by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean updateBalance(BankAccount item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement balanceUpdate = conn.prepareStatement(
                  "UPDATE bankaccounts "+
                  "SET balance = ? "+
                  "WHERE accountid = ?;"
            );

            balanceUpdate.setBigDecimal(1, item.balance());
            balanceUpdate.setLong(2, item.accountID());

            balanceUpdate.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to update account balance: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean save(BankAccount item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement createBankAccount = conn.prepareStatement(
                "INSERT INTO bankaccounts (userid, bankid, balance) "+
                "VALUES (?, ?, ?);"
            );

            createBankAccount.setLong(1, item.userID());
            createBankAccount.setLong(2, item.bankID());
            createBankAccount.setBigDecimal(3, item.balance());

            createBankAccount.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to get save new bank account : " + e.getMessage());
        }

        return false;
    }
}
