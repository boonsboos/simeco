package nl.boonsboos.simeco.controllers;

import nl.boonsboos.simeco.auth.AuthClient;
import nl.boonsboos.simeco.auth.ClientStore;
import nl.boonsboos.simeco.controllers.responses.error.UserUnauthorizedResponse;
import nl.boonsboos.simeco.data.UserDAO;
import nl.boonsboos.simeco.entities.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class UserController {

    private static final UserDAO USER_DAO = new UserDAO();

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader(HttpHeaders.AUTHORIZATION) String key) {
        AuthClient client = ClientStore.getClientByKey(key);
        if (client == null) return ResponseEntity.status(401).body(new UserUnauthorizedResponse("/me"));

        return ResponseEntity.ok(USER_DAO.get(client.userID()));
    }

}
