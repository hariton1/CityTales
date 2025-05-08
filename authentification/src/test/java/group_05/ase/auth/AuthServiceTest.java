package group_05.ase.auth;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthServiceTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

    }

    @Test
    public void testRegisterAndLogin() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("securePass123");

        authService.register(req);
        assertTrue(userRepository.findByEmail("test@example.com").isPresent());

        LoginRequest login = new LoginRequest();
        login.setEmail("test@example.com");
        login.setPassword("securePass123");

        String token = authService.login(login);
        assertNotNull(token);
    }

    @Test
    public void testDuplicateRegistrationFails() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("duplicate@example.com");
        req.setPassword("pass");
        authService.register(req);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    public void testLoginFailsWithWrongPassword() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("wrongpass@example.com");
        req.setPassword("correct");
        authService.register(req);

        LoginRequest login = new LoginRequest();
        login.setEmail("wrongpass@example.com");
        login.setPassword("incorrect");

        assertThrows(RuntimeException.class, () -> authService.login(login));
    }
}
