package nl.boonsboos.simeco.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.controllers.responses.bank.BankTransferResponse;
import nl.boonsboos.simeco.controllers.responses.bank.PaginatedBankAccountListResponse;
import nl.boonsboos.simeco.controllers.responses.error.UserUnauthorizedResponse;
import nl.boonsboos.simeco.data.bank.BankAccountDAO;
import nl.boonsboos.simeco.data.bank.BankDAO;
import nl.boonsboos.simeco.data.bank.TransactionDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;
import nl.boonsboos.simeco.entities.bank.Transaction;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@RestController
@RequestMapping("/bankaccounts")
public class BankAccountController {

    private static final Logger LOG = Logger.getLogger(BankController.class.getSimpleName());

    private static final BankAccountDAO BANK_ACCOUNT_DAO = new BankAccountDAO();
    private static final TransactionDAO TRANSACTION_DAO = new TransactionDAO();

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getBankAccounts(@RequestParam(required = false) Integer page, @RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/bankaccounts"));
        }

        if (page == null) {
            page = 1;
        }

        return ResponseEntity.ok(
            new PaginatedBankAccountListResponse(
                page,
                BANK_ACCOUNT_DAO.getBankAccounts(page, client.userID())
            )
        );
    }

    /**
     * Fetches account detail about an account the user owns
     * @param account the account number
     * @param key the API key of the user
     * @return bank account if found and authorized.
     */
    @GetMapping("/{account:[0-9]{6}[A-Z]{4,5}[0-9]{6}-[0-9]+}")
    @ResponseBody
    public ResponseEntity<?> getBankAccountByNumber(@PathVariable String account, @RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/bankaccounts/"+account));
        }

        BankAccount bankAccount = BANK_ACCOUNT_DAO.getByAccountNumber(account, client.userID());
        if (bankAccount == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping("/transfer")
    @ResponseBody
    public ResponseEntity<?> transferMoney(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String amount,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String key
    ) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/transfer"));
        }

        BankAccount fromBankAccount = BANK_ACCOUNT_DAO.getByAccountNumber(from, client.userID());
        BankAccount toBankAccount = BANK_ACCOUNT_DAO.getByAccountNumber(to);
        if (fromBankAccount == null || toBankAccount == null) {
            return ResponseEntity.notFound().build();
        }

        BigDecimal transferAmount;
        try {
            transferAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        // check if we are not transferring negative amounts of currency
        if (transferAmount.signum() < 1) {
            return ResponseEntity.badRequest().build();
        }

        // check if from has enough balance to complete the transaction
        if (fromBankAccount.balance().compareTo(transferAmount) < 1) {
            return ResponseEntity.badRequest().body(new BankTransferResponse(from, to, BankTransferResponse.INSUFFICIENT_BALANCE));
        }

        if (BANK_ACCOUNT_DAO.transfer(fromBankAccount, toBankAccount, transferAmount)) {
            // negate for outgoing
            TRANSACTION_DAO.save(new Transaction(
                fromBankAccount.accountID(), toBankAccount.accountID(),
                transferAmount.negate(), false // outgoing
            ));
            TRANSACTION_DAO.save(new Transaction(
                fromBankAccount.accountID(), toBankAccount.accountID(),
                transferAmount, true // incoming
            ));
            return ResponseEntity.ok(new BankTransferResponse(from, to, BankTransferResponse.TRANSACTION_SUCCESS));
        }

        return ResponseEntity.internalServerError().body(new BankTransferResponse(from, to, BankTransferResponse.TRANSACTION_FAILED));
    }
}
