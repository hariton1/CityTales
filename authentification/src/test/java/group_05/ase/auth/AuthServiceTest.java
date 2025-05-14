package group_05.ase.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private SupabaseService supabaseService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCreatesUserAndReturnsJwt() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("test123");
        request.setSupabaseId("123e4567-e89b-12d3-a456-426614174000");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(supabaseService.createUser("test@test.com", "test123")).thenReturn("123e4567-e89b-12d3-a456-426614174000");
        AppUser savedUser = AppUser.builder().email("test@test.com").supabaseId(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000")).build();
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        String jwt = authService.register(request);

        assertEquals("jwt-token", jwt);
    }

    @Test
    void loginReturnsJwtOnSupabaseSuccess() {
        LoginRequest req = new LoginRequest();
        req.setEmail("login@test.com");
        req.setPassword("test123");

        AppUser user = AppUser.builder()
                .email("login@test.com")
                .supabaseId(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        when(userRepository.findByEmail("login@test.com")).thenReturn(Optional.of(user));
        when(supabaseService.loginUser("login@test.com", "test123")).thenReturn("supabase-token"); // Success
        when(jwtService.generateToken(user)).thenReturn("jwt-jwt");

        String jwt = authService.login(req);

        assertEquals("jwt-jwt", jwt);
    }

    @Test
    void loginThrowsOnSupabaseFailure() {
        LoginRequest req = new LoginRequest();
        req.setEmail("login@test.com");
        req.setPassword("wrongpw");

        AppUser user = AppUser.builder()
                .email("login@test.com")
                .supabaseId(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .build();

        when(userRepository.findByEmail("login@test.com")).thenReturn(Optional.of(user));
        when(supabaseService.loginUser("login@test.com", "wrongpw"))
                .thenThrow(new BadCredentialsException("Invalid password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }
}
