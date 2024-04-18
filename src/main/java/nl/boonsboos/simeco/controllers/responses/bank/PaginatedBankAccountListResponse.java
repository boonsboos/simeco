package nl.boonsboos.simeco.controllers.responses.bank;

import nl.boonsboos.simeco.entities.bank.BankAccount;

import java.util.List;

public record PaginatedBankAccountListResponse(long page, List<BankAccount> accounts) { }