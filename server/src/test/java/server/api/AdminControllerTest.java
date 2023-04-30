package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.services.RandomService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private RandomService randomService;

    private AdminController adminController;
    private final String password = "correct_password";


    @BeforeEach
    public void setUp() {
        when(randomService.getRandomString()).thenReturn(password);
        adminController = new AdminController(randomService);
    }

    @Test
    public void testValidatePasswordWithCorrectPassword() {
        ResponseEntity<Boolean> response = adminController.validatePassword(password);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    public void testValidatePasswordWithIncorrectPassword() {
        String incorrectPassword = "incorrect_password";
        ResponseEntity<Boolean> response = adminController.validatePassword(incorrectPassword);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    public void testValidatePasswordWithNullPassword() {
        ResponseEntity<Boolean> response = adminController.validatePassword(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testValidatePasswordWithEmptyPassword() {
        ResponseEntity<Boolean> response = adminController.validatePassword("");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
