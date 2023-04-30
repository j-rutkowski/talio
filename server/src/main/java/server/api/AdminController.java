package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.services.RandomService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final String serverPassword;

    /**
     * Constructs a new admin controller
     *
     * @param randomService service for generating random strings
     */
    public AdminController(RandomService randomService) {
        serverPassword = randomService.getRandomString();
        System.out.println("Server password: " + serverPassword);
    }

    /**
     * Endpoint for validating the server password
     *
     * @param password the password to validate
     * @return true if the password is correct, false otherwise
     */
    @PostMapping
    public ResponseEntity<Boolean> validatePassword(@RequestParam String password) {
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(password.equals(serverPassword));
    }
}
