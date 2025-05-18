package group_05.ase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;
    @Mock
    private SupabaseService supabaseService;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- REGISTER TESTS ---

    @Test
    void registerCreatesUserAndReturnsJwt() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@test.com");
        request.setPassword("pw123");

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(supabaseService.createUser("new@test.com", "pw123"))
                .thenReturn("c3b9d7ad-b7da-432d-9c7c-79f50a6ea731");

        AppUser savedUser = AppUser.builder()
                .email("new@test.com")
                .supabaseId(UUID.fromString("c3b9d7ad-b7da-432d-9c7c-79f50a6ea731"))
                .build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        String jwt = authService.register(request);
        assertEquals("jwt-token", jwt);

        verify(userRepository).save(any(AppUser.class));
        verify(supabaseService).createUser("new@test.com", "pw123");
    }

    @Test
    void registerThrowsIfEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("exist@test.com");
        request.setPassword("pw123");

        when(userRepository.findByEmail("exist@test.com"))
                .thenReturn(Optional.of(AppUser.builder().email("exist@test.com").build()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
        verify(supabaseService, never()).createUser(anyString(), anyString());
    }

    // --- LOGIN TESTS ---

    @Test
    void loginReturnsJwtIfUserExistsAndSupabaseOk() {
        LoginRequest request = new LoginRequest();
        request.setEmail("login@test.com");
        request.setPassword("pw123");

        when(supabaseService.loginUser("login@test.com", "pw123")).thenReturn("supabase-token");

        AppUser user = AppUser.builder()
                .email("login@test.com")
                .supabaseId(UUID.fromString("c3b9d7ad-b7da-432d-9c7c-79f50a6ea732"))
                .build();

        when(userRepository.findByEmail("login@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token-login");

        String jwt = authService.login(request);
        assertEquals("jwt-token-login", jwt);
        verify(supabaseService).loginUser("login@test.com", "pw123");
    }

    @Test
    void loginThrowsIfUserNotInLocalDb() {
        LoginRequest request = new LoginRequest();
        request.setEmail("no-user@test.com");
        request.setPassword("pw123");

        when(supabaseService.loginUser("no-user@test.com", "pw123")).thenReturn("supabase-token");
        when(userRepository.findByEmail("no-user@test.com")).thenReturn(Optional.empty());

        Exception e = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertTrue(e.getMessage().contains("User nicht in App DB gefunden"));
    }

    // Optional: Simulate Supabase login error (throws)
    @Test
    void loginThrowsIfSupabaseLoginFails() {
        LoginRequest request = new LoginRequest();
        request.setEmail("supabase-fail@test.com");
        request.setPassword("pw123");

        when(supabaseService.loginUser("supabase-fail@test.com", "pw123"))
                .thenThrow(new RuntimeException("Supabase down!"));

        Exception e = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertTrue(e.getMessage().contains("Supabase down!"));
    }

    // --- REFRESH TOKEN TESTS ---

    @Test
    void refreshAccessTokenReturnsJwtIfValid() {
        String tokenStr = "token-ok";
        UUID userId = UUID.randomUUID();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenStr)
                .userId(userId)
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        AppUser user = AppUser.builder()
                .supabaseId(userId)
                .email("user@refresh.com")
                .build();

        when(refreshTokenService.findByToken(tokenStr)).thenReturn(Optional.of(refreshToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-refreshed");

        String jwt = authService.refreshAccessToken(tokenStr);
        assertEquals("jwt-refreshed", jwt);
    }

    @Test
    void refreshAccessTokenThrowsIfTokenNotFound() {
        when(refreshTokenService.findByToken("not-found")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken("not-found"));
    }

    @Test
    void refreshAccessTokenThrowsIfTokenRevoked() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .revoked(true)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenService.findByToken("revoked-token")).thenReturn(Optional.of(token));
        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken("revoked-token"));
    }

    @Test
    void refreshAccessTokenThrowsIfTokenExpired() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .revoked(false)
                .expiresAt(Instant.now().minusSeconds(1))
                .build();

        when(refreshTokenService.findByToken("expired-token")).thenReturn(Optional.of(token));
        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken("expired-token"));
    }

    @Test
    void refreshAccessTokenThrowsIfUserNotFound() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenService.findByToken("valid-token")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.refreshAccessToken("valid-token"));
    }
}
