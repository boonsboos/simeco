package nl.boonsboos.simeco.controllers;

import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.controllers.responses.bank.PaginatedBankListResponse;
import nl.boonsboos.simeco.controllers.responses.error.UserUnauthorizedResponse;
import nl.boonsboos.simeco.data.entities.bank.BankAccountDAO;
import nl.boonsboos.simeco.data.entities.bank.BankDAO;
import nl.boonsboos.simeco.entities.bank.Bank;
import nl.boonsboos.simeco.entities.bank.BankAccount;
import nl.boonsboos.simeco.util.AccountNumberGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/banks")
public class BankController {

    private static final Logger LOG = Logger.getLogger(BankController.class.getSimpleName());

    private static final BankDAO BANK_DAO = new BankDAO();
    private static final BankAccountDAO BANK_ACCOUNT_DAO = new BankAccountDAO();

    /**
     * Gets a list of the most popular banks.
     * @param page the page of the list
     * @param key the auth key of the user
     * @return {@link PaginatedBankListResponse} with banks
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getBanks(@RequestParam(required = false) Integer page, @RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        // check if user is authorized to view this content
        if (ClientStore.getClientByKey(key) == null) {
            return  ResponseEntity.status(401).body(new UserUnauthorizedResponse("/banks"));
        }

        if (page == null) {
            page = 1;
        }

        return ResponseEntity.ok(new PaginatedBankListResponse(page, BANK_DAO.getPopularBanks(page)));
    }

    /**
     * Gets a {@link Bank} by its bank initials
     * @param query the initials or the name of the bank
     * @return bank if found, not found otherwise
     */
    @GetMapping("/search/{query:[A-Za-z\\s]+}")
    @ResponseBody
    public ResponseEntity<?> getBankByNameOrInitials(
            @PathVariable String query,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String key,
            @RequestParam(required = false) Integer page
    ) {
        if (ClientStore.getClientByKey(key) == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/banks/"+query));
        }

        String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8);

        LOG.info("Decoded query string:" +decodedQuery);

        if (page == null) {
            page = 1;
        }

        // add wildcards for LIKE search
        List<Bank> banks = BANK_DAO.get(page, "%"+decodedQuery.replace(' ', '%')+"%");
        return ResponseEntity.ok(new PaginatedBankListResponse(1, banks));
    }

    @GetMapping("/{bankInitials:[A-Z]{4,5}}/open")
    @ResponseBody
    public ResponseEntity<?> openAccountAt(@PathVariable String bankInitials, @RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/banks/open/"+bankInitials));
        }

        String decodedQuery = URLDecoder.decode(bankInitials, StandardCharsets.UTF_8);
        Bank bank = BANK_DAO.get("%"+decodedQuery+"%");
        if (bank == null) {
            return ResponseEntity.internalServerError().build();
        }

        // save and return bank account data to user
        BankAccount bankAccount = new BankAccount(
            -1, client.userID(), bank.bankID(),
            AccountNumberGenerator.newAccountNumber(bank, client.userID()), BigDecimal.ZERO
        );
        BANK_ACCOUNT_DAO.save(bankAccount);

        return ResponseEntity.ok(bankAccount);
    }
}