package nl.boonsboos.simeco.data.entities.bank;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TransactionDAO implements SimecoDAO<Transaction> {

    private static final Logger LOG = Logger.getLogger(TransactionDAO.class.getSimpleName());

    @Override
    public Transaction get(long id) {
        return null;
    }

    /**
     * Represents a transaction with the account numbers populated.
     * @param from account number from
     * @param to account number to
     * @param amount amount of currency
     * @param timestamp time of the transaction
     */
    public record PopulatedTransaction(String from, String to, BigDecimal amount, LocalDateTime timestamp) { }

    public List<PopulatedTransaction> getTransactionHistory(long accountID, long page) {

        // turn page into offset
        long offset = (page-1) * 10;

        List<PopulatedTransaction> list = new ArrayList<>();

        try (Connection conn = DatabasePool.getConnection()) {
            // shoutout to LostAlgorithm in zozen discord for assisting in writing this
            PreparedStatement getFullTransactionHistory = conn.prepareStatement(
                """
                SELECT a.accountnumber AS afrom, b.accountnumber AS bto, amount, transactiontime FROM transactions
                LEFT JOIN bankaccounts AS a ON a.accountid = fromid
                LEFT JOIN bankaccounts AS b ON b.accountid = toid
                WHERE fromid = ? OR toid = ?
                ORDER BY transactiontime DESC
                LIMIT 20 OFFSET ?;
                """
            );

            getFullTransactionHistory.setLong(1, accountID);
            getFullTransactionHistory.setLong(2, accountID);
            getFullTransactionHistory.setLong(3, offset);

            ResultSet result = getFullTransactionHistory.executeQuery();

            while (result.next()) {
                list.add(new PopulatedTransaction(
                    result.getString("afrom"),
                    result.getString("bto"),
                    result.getBigDecimal("amount"),
                    result.getTimestamp("transactiontime").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            LOG.warning("Failed to fetch populated transactions: "+e.getMessage());
        }

        return list;
    }

    /**
     * Saves a transaction to the database
     * @param item the item to save
     * @return true on success, false otherwise
     */
    @Override
    public boolean save(Transaction item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement insertTransaction = conn.prepareStatement(
                "INSERT INTO transactions (fromid, toid, amount, transactiontime) "+
                "VALUES (?, ?, ?, ?);"
            );

            insertTransaction.setLong(1, item.accountFrom());
            insertTransaction.setLong(2, item.accountTo());
            insertTransaction.setBigDecimal(3, item.amount());
            insertTransaction.setTimestamp(4, Timestamp.valueOf(item.timestamp()));

            insertTransaction.execute();

            return true;

        } catch (SQLException e) {
            LOG.warning("Failed to save transaction:" + e.getMessage());
        }

        return false;
    }
}
