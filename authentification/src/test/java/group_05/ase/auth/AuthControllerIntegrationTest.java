
package group_05.ase.auth;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class AuthControllerIntegrationTest {

    @Autowired AuthService authService;
    @Autowired RefreshTokenService refreshTokenService;
    @Autowired AppUserRepository userRepository;
    @Autowired JwtService jwtService;

    @Test
    void testRefreshAndLogout() {
        // 1. User registrieren (deine Methode!)
        RegisterRequest regReq = new RegisterRequest();
        regReq.setEmail("refresh@example.com");
        regReq.setPassword("Test123!");
        String jwt = authService.register(regReq);

        AppUser user = userRepository.findByEmail("refresh@example.com").orElseThrow();

        // 2. Refresh Token anlegen
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenService.createToken(user.getId(), refreshToken, Instant.now().plusSeconds(60 * 60 * 24));

        // 3. Token refreshen
        String newJwt = authService.refreshAccessToken(refreshToken);
        assertNotNull(newJwt);

        // 4. Logout (Revoke Token)
        refreshTokenService.revokeToken(refreshToken);
        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken(refreshToken));
    }
}
