package nl.boonsboos.simeco.controllers.responses.bank;

import nl.boonsboos.simeco.data.entities.bank.TransactionDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;

import java.util.List;

public record BankAccountTransactionHistory(
        BankAccount account,
        List<TransactionDAO.PopulatedTransaction> recentTransactions
) { }
