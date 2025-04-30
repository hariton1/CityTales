package group_05.ase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldRegisterUser() {
        RegisterRequest req = new RegisterRequest();
        req.email = "test@example.com";
        req.password = "test123";

        authService.register(req);
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    public void shouldLoginUser() {
        RegisterRequest req = new RegisterRequest();
        req.email = "login@example.com";
        req.password = "secure";

        authService.register(req);

        LoginRequest login = new LoginRequest();
        login.email = req.email;
        login.password = req.password;

        String token = authService.login(login);
        assertThat(token).isNotEmpty();
    }
}
