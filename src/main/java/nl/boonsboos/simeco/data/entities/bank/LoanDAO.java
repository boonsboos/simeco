package nl.boonsboos.simeco.data.entities.bank;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LoanDAO implements SimecoDAO<Loan> {

    private static final Logger LOG = Logger.getLogger(LoanDAO.class.getSimpleName());

    @Override
    public Loan get(long id) {
        return null;
    }

    public Loan getByBankAccountID(long accountID) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getByAccount = conn.prepareStatement(
                """
                SELECT * FROM loans
                WHERE accountid = ?;
                """
            );

            getByAccount.setLong(1, accountID);

            ResultSet result = getByAccount.executeQuery();
            if (result.next()) {
                return new Loan(
                    result.getLong("loanid"),
                    result.getLong("bankid"),
                    result.getLong("accountid"),
                    result.getBigDecimal("remainingamount")
                );
            }

        } catch (SQLException e) {
            LOG.warning("Failed to save loan: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean save(Loan item) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement saveLoan = conn.prepareStatement(
                """
                INSERT INTO loans (bankid, accountid, remainingamount)
                VALUES (?, ?, ?);
                """
            );

            saveLoan.setLong(1, item.getBankID());
            saveLoan.setLong(2, item.getAccountID());
            saveLoan.setBigDecimal(3, item.getRemainingAmount());

            saveLoan.execute();

            return true;
        } catch (SQLException e) {
            LOG.warning("Failed to save loan: " + e.getMessage());
        }

        return false;
    }
}
