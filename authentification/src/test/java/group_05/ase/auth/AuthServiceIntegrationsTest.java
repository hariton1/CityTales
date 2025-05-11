package group_05.ase.auth;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Rollback nach jedem Test (empfohlen für Integrationstests)
class AuthServiceIntegrationTest {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    AuthService authService;

    @Autowired
    JwtService jwtService;

    @Autowired
    SupabaseService supabaseService;

    String testEmail = "integrationtest@example.com";

    @BeforeEach
    void cleanUpBefore() {
        // Optional: Clean-up oder Setup, falls nötig
        userRepository.deleteAll();
    }

    @Test
    void registerAndLoginWithSupabaseAndJwt() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(testEmail);
        registerRequest.setPassword("MySecret123!");

        String jwt = authService.register(registerRequest);
        assertNotNull(jwt, "JWT sollte zurückgegeben werden");

        Optional<AppUser> createdUserOpt = userRepository.findByEmail("integrationtest@example.com");
        assertTrue(createdUserOpt.isPresent(), "User sollte in der DB gespeichert sein");
        AppUser createdUser = createdUserOpt.get();
        assertNotNull(createdUser.getSupabaseId(), "SupabaseId sollte gesetzt sein");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword("MySecret123!");

        String jwt2 = authService.login(loginRequest);
        assertNotNull(jwt2, "Login sollte JWT zurückgeben");

        // 4. Login mit falschem Passwort (soll Exception werfen)
        LoginRequest wrongLogin = new LoginRequest();
        wrongLogin.setEmail("integrationtest@example.com");
        wrongLogin.setPassword("wrongPassword");
        assertThrows(Exception.class, () -> authService.login(wrongLogin));
    }

    @Test
    void userRepositorySavesAndFindsUser() {
        AppUser user = AppUser.builder()
                .email("findme@example.com")
                .supabaseId(UUID.randomUUID())
                .build();

        userRepository.save(user);

        Optional<AppUser> found = userRepository.findByEmail("findme@example.com");
        assertTrue(found.isPresent());
        assertEquals(user.getEmail(), found.get().getEmail());
    }

    @AfterEach
    void cleanupAfter() {
        userRepository.findByEmail(testEmail).ifPresent(user -> {
            if (user.getSupabaseId() != null) {
                supabaseService.deleteUser(user.getSupabaseId());
            }
        });
        userRepository.deleteAll();
    }

}
