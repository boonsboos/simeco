package nl.boonsboos.simeco.controllers;

import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.controllers.responses.error.UserUnauthorizedResponse;
import nl.boonsboos.simeco.data.entities.bank.BankAccountDAO;
import nl.boonsboos.simeco.data.entities.bank.LoanDAO;
import nl.boonsboos.simeco.entities.bank.BankAccount;
import nl.boonsboos.simeco.entities.bank.Loan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private static final Logger LOG = Logger.getLogger(LoanController.class.getSimpleName());

    private static final BankAccountDAO BANK_ACCOUNT_DAO = new BankAccountDAO();
    private static final LoanDAO LOAN_DAO = new LoanDAO();

    /**
     * Fetches the user's current loans
     */
    @GetMapping
    public ResponseEntity<?> getCurrentLoans(@RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) {
            return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/transfer"));
        }

        List<Loan> loans = new ArrayList<>();

        for (BankAccount b : BANK_ACCOUNT_DAO.getBankAccounts(client.userID())) {
            Loan loan = LOAN_DAO.getByBankAccountID(b.accountID());
            if (loan != null) loans.add(loan);
        }

        return ResponseEntity.ok(loans);
    }
}
