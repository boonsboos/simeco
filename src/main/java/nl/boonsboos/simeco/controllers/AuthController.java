package nl.boonsboos.simeco.controllers;

import nl.boonsboos.simeco.Simeco;
import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.auth.data.AuthClientDAO;
import nl.boonsboos.simeco.data.UserDAO;
import nl.boonsboos.simeco.entities.User;
import nl.boonsboos.simeco.util.GitHubRequests;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.logging.Logger;

@RestController
public class AuthController {

    private static final Logger LOG = Logger.getLogger(AuthClient.class.getSimpleName());

    private static final UserDAO USER_DAO = new UserDAO();
    private static final AuthClientDAO AUTH_CLIENT_DAO = new AuthClientDAO();

    private static final String GITHUB_PORTAL_URL = "https://github.com/login/oauth/authorize?client_id="+
            Simeco.CONFIG.getGithubID()+
            "&redirect_uri="+
            "http://localhost:8080/submit"+ // FIXME: prod URL
            "&scope=read:user"+
            "&allow_signup=true";

    @GetMapping("/login")
    public RedirectView handleSignup() {
        return new RedirectView(GITHUB_PORTAL_URL);
    }

    @GetMapping("/submit")
    public AuthClient finishSignup(@RequestParam String code) {

        String username = new GitHubRequests.GitHubUserName(code).getUsername();

        // create new entry in db
        USER_DAO.save(new User(username));
        // populate with ID
        User u = USER_DAO.get(username);

        if (u == null) {
            return new AuthClient(-1, "Something went wrong!");
        }

        ClientStore.saveNewClient(u);

        return AUTH_CLIENT_DAO.get(u.getUserID());
    }
}
