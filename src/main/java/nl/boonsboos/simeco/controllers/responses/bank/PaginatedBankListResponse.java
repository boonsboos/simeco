package nl.boonsboos.simeco.controllers.responses.bank;

import nl.boonsboos.simeco.entities.bank.Bank;

import java.util.List;

public record PaginatedBankListResponse(long page, List<Bank> banks) { }