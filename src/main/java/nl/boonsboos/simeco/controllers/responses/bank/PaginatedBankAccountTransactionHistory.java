package nl.boonsboos.simeco.controllers.responses.bank;

import nl.boonsboos.simeco.data.entities.bank.TransactionDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;

import java.util.List;

public record PaginatedBankAccountTransactionHistory(
        BankAccount account,
        Integer page,
        List<TransactionDAO.PopulatedTransaction> transactions
) { }