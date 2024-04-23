package nl.boonsboos.simeco.data.entities.bank;

import nl.boonsboos.simeco.data.DatabasePool;
import nl.boonsboos.simeco.data.SimecoDAO;
import nl.boonsboos.simeco.entities.bank.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BankDAO implements SimecoDAO<Bank> {

    private static final Logger LOG = Logger.getLogger(BankDAO.class.getSimpleName());

    @Override
    public Bank get(long id) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getBank = conn.prepareStatement(
                "SELECT * FROM banks "+
                "WHERE bankid = ?;"
            );

            getBank.setLong(1, id);

            ResultSet result = getBank.executeQuery();

            if (result.next()) {
                return new Bank(
                    result.getLong("bankid"),
                    result.getString("bankname"),
                    result.getString("bankinitials"),
                    result.getFloat("depositinterest"),
                    result.getFloat("loaninterest"),
                    result.getBigDecimal("vaultbalance")
                );
            }

        } catch (SQLException e) {
            LOG.warning("Failed to get bank by its id: "+e.getMessage());
        }

        return null;
    }

    /**
     * Gets banks by name or initials
     * @param identifier the name or initials, with wildcards
     * @return related banks if found, empty list otherwise
     */
    public Bank get(String identifier) {
        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getBanks = conn.prepareStatement(
                "SELECT * FROM banks "+
                "WHERE bankinitials LIKE ? "+
                "OR bankname LIKE ?;"
            );

            getBanks.setString(1, identifier);
            getBanks.setString(2, identifier);

            ResultSet result = getBanks.executeQuery();

            if (result.next()) {
                return new Bank(
                    result.getLong("bankid"),
                    result.getString("bankname"),
                    result.getString("bankinitials"),
                    result.getFloat("depositinterest"),
                    result.getFloat("loaninterest"),
                    result.getBigDecimal("vaultbalance")
                );
            }

        } catch (SQLException e) {
            LOG.warning("Failed to get bank by its id: "+e.getMessage());
        }

        return null;
    }

    /**
     * Gets banks by name or initials
     * @param identifier the name or initials, with wildcards
     * @return related banks if found, empty list otherwise
     */
    public List<Bank> get(int page, String identifier) {
        List<Bank> list = new ArrayList<>();

        page = (page - 1) * 10;

        try (Connection conn = DatabasePool.getConnection()) {
            PreparedStatement getBanks = conn.prepareStatement(
                "SELECT * FROM banks "+
                "WHERE bankinitials LIKE ? "+
                "OR bankname LIKE ? "+
                "LIMIT 10 OFFSET ?;"
            );

            getBanks.setString(1, identifier);
            getBanks.setString(2, identifier);
            getBanks.setLong(3, page);

            ResultSet result = getBanks.executeQuery();

            while (result.next()) {
                list.add(new Bank(
                    result.getLong("bankid"),
                    result.getString("bankname"),
                    result.getString("bankinitials"),
                    result.getFloat("depositinterest"),
                    result.getFloat("loaninterest"),
                    result.getBigDecimal("vaultbalance")
                ));
            }

        } catch (SQLException e) {
            LOG.warning("Failed to get bank by its id: "+e.getMessage());
        }

        return list;
    }

    /**
     * Tries to get the 10 most popular banks (where more people have an account)
     * @param offset a page
     * @return up to 10 of the most popular banks, empty list otherwise
     */
    public List<Bank> getPopularBanks(long offset) {

        offset = (offset - 1) * 10;

        List<Bank> list = new ArrayList<>();

        try (Connection conn = DatabasePool.getConnection()) {
            // is this query good? no.
            // does it work? yeah, it's fine
            PreparedStatement getPopular = conn.prepareStatement(
                "SELECT banks.bankid, bankname, bankinitials, depositinterest, loaninterest, vaultbalance FROM banks "+
                "JOIN (\n"+
                    "SELECT bankid, COUNT(bankid) AS count FROM bankaccounts GROUP BY bankid ORDER BY count DESC LIMIT 10\n"+ // TODO: create a clustered index for this
                ") counts ON banks.bankid = counts.bankid "+
                "ORDER BY count DESC "+
                "LIMIT 10 OFFSET ?;"
            );

            getPopular.setLong(1, offset);

            ResultSet result = getPopular.executeQuery();

            while (result.next()) {
                list.add(new Bank(
                    result.getLong("bankid"),
                    result.getString("bankname"),
                    result.getString("bankinitials"),
                    result.getFloat("depositinterest"),
                    result.getFloat("loaninterest"),
                    result.getBigDecimal("vaultbalance")
                ));
            }
        } catch (SQLException e) {
            LOG.warning("Failed to get popular banks: " + e.getMessage());
        }

        return list;
    }

    @Override
    public boolean save(Bank item) {
        return false;
    }
}
