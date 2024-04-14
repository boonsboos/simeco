package nl.boonsboos.simeco.data.bank;

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
        return null;
    }

    /**
     * Tries to get the 10 most popular banks (where more people have an account)
     * @return up to 10 of the most popular banks, empty list otherwise
     */
    public List<Bank> getPopularBanks() {

        List<Bank> list = new ArrayList<>();

        try (Connection conn = DatabasePool.getConnection()) {
            // is this query good? no.
            // does it work? yeah, it's fine
            PreparedStatement getPopular = conn.prepareStatement(
                "SELECT banks.bankid, bankname, bankinitials, depositinterest, loaninterest, vaultbalance FROM banks "+
                "JOIN (\n"+
                    "SELECT bankid, COUNT(bankid) AS count FROM bankaccounts GROUP BY bankid ORDER BY count DESC LIMIT 10\n"+
                ") counts ON banks.bankid = counts.bankid "+
                "ORDER BY count DESC "+
                "LIMIT 10;"
            );

            ResultSet result = getPopular.executeQuery();

            while (result.next()) {
                list.add(new Bank(
                        result.getLong("bankid"),
                        result.getString("bankname"),
                        result.getString("bankinitials"),
                        result.getFloat("depositinterest"),
                        result.getFloat("loaninterest"),
                        result.getBigDecimal("vaultbalance")
                    )
                );
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
