package group_05.ase.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceIntegrationsTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    private final List<String> createdEmails = new ArrayList<>();

    @AfterEach
    void cleanupAfter() {
        for (String email : createdEmails) {
            userRepository.findByEmail(email).ifPresent(userRepository::delete);
        }
        createdEmails.clear();
    }

    @Test
    void shouldRegisterAndLoginViaService() {
        String email = "serviceint-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail(email);
        regReq.setPassword(password);

        String jwt = authService.register(regReq);
        assertNotNull(jwt);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(email);
        loginReq.setPassword(password);

        String jwt2 = authService.login(loginReq);
        assertNotNull(jwt2);
    }

    @Test
    void shouldFailRegisterWithDuplicateEmail() {
        String email = "dupserviceint-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail(email);
        regReq.setPassword(password);

        String jwt = authService.register(regReq);
        assertNotNull(jwt);

        RegisterRequest regReq2 = new RegisterRequest();
        regReq2.setEmail(email);
        regReq2.setPassword(password);

        assertThrows(RuntimeException.class, () -> authService.register(regReq2));
    }

    @Test
    void shouldRefreshTokenAndRevoke() {
        String email = "refreshint-" + UUID.randomUUID() + "@test.com";
        String password = "pw123";
        createdEmails.add(email);

        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail(email);
        regReq.setPassword(password);

        String jwt = authService.register(regReq);
        assertNotNull(jwt);

        AppUser user = userRepository.findByEmail(email).orElseThrow();
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createToken(user.getId(), refreshToken, Instant.now().plusSeconds(3600));

        // Should work
        String newJwt = authService.refreshAccessToken(refreshToken);
        assertNotNull(newJwt);

        // Revoke
        refreshTokenService.revokeToken(refreshToken);

        // Should fail now
        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken(refreshToken));
    }
}
