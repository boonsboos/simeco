package nl.boonsboos.simeco.data.bank;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public List<BankAccount> getBankAccounts(long offset, long userid) {

        offset = (offset - 1) * 20;

        List<BankAccount> list = new ArrayList<>();

        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getAccounts = conn.prepareStatement(
                "SELECT * FROM bankaccounts "+
                "WHERE userid = ? "+
                "LIMIT 20 OFFSET ?;"
            );

            getAccounts.setLong(1, userid);
            getAccounts.setLong(2, offset);

            ResultSet result = getAccounts.executeQuery();

            while (result.next()) {
                list.add(new BankAccount(
                    result.getLong("accountid"),
                    result.getLong("userid"),
                    result.getLong("bankid"),
                    result.getString("accountnumber"),
                    result.getBigDecimal("balance")
                ));
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get bank accounts: " + e.getMessage());
        }

        return list;
    }

    /**
     * Gets a user's bank account by their account number while verifying they own the account
     * @param accountNumber the number of the account
     * @param userID the id of the owner of the account
     * @return {@link BankAccount} if found, null otherwise
     */
    public BankAccount getByAccountNumber(String accountNumber, long userID) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getByNumber = conn.prepareStatement(
                "SELECT * FROM bankaccounts "+
                "WHERE accountNumber = ? "+
                "AND userid = ?;" // checks with user ID as well
                // if a bad actor tries to check for a
            );

            getByNumber.setString(1, accountNumber);
            getByNumber.setLong(2, userID);

            ResultSet result = getByNumber.executeQuery();

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
            LOG.warning("Failed to fetch account by account number with user ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets a user's bank account by their account number without verifying they own the account
     * @param accountNumber the number of the account
     * @return {@link BankAccount} if found, null otherwise
     */
    public BankAccount getByAccountNumber(String accountNumber) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getByNumber = conn.prepareStatement(
                    "SELECT * FROM bankaccounts "+
                    "WHERE accountNumber = ? ;"
            );

            getByNumber.setString(1, accountNumber);

            ResultSet result = getByNumber.executeQuery();

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
            LOG.warning("Failed to fetch account by account number: " + e.getMessage());
        }

        return null;
    }

    public boolean transfer(BankAccount from, BankAccount to, BigDecimal amount) {
        try (Connection conn = DatabasePool.getConnection()) {
            // this query is highly sensitive to changes.
            // we must therefore queue the queries
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            PreparedStatement transferFromAToB = conn.prepareStatement(
                // account From
                "UPDATE bankaccounts "+
                "SET balance = balance - ? "+
                "WHERE accountid = ?;"+
                // account To
                "UPDATE bankaccounts "+
                "SET balance = balance + ? "+
                "WHERE accountid = ?;"
            );

            transferFromAToB.setBigDecimal(1, amount);
            transferFromAToB.setBigDecimal(3, amount);

            transferFromAToB.setLong(2, from.accountID());
            transferFromAToB.setLong(4, to.accountID());

            // if fails, must be rolled back
            transferFromAToB.execute();

            // success
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
                "INSERT INTO bankaccounts (userid, bankid, balance, accountnumber) "+
                "VALUES (?, ?, ?, ?);"
            );

            createBankAccount.setLong(1, item.userID());
            createBankAccount.setLong(2, item.bankID());
            createBankAccount.setBigDecimal(3, item.balance());
            createBankAccount.setString(4, item.accountNumber());

            createBankAccount.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to get save new bank account: " + e.getMessage());
        }

        return false;
    }
}
