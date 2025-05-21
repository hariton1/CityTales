
package group_05.ase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final SupabaseService supabaseService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create the user on Supabase
        String supabaseId = supabaseService.createUser(request.getEmail(), request.getPassword());

        // Ensure the supabaseId is not null or invalid before proceeding
        if (supabaseId == null || supabaseId.isEmpty()) {
            throw new RuntimeException("Failed to create user on Supabase, ID is null or empty");
        }

        // Continue with the registration process
        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .supabaseId(UUID.fromString(supabaseId))  // Convert the ID to UUID
                .build();
        userRepository.save(user);

        return jwtService.generateToken(user);  // Return the generated JWT token
    }



    public String login(LoginRequest request) {
        String supabaseToken = supabaseService.loginUser(request.getEmail(), request.getPassword());

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User nicht in App DB gefunden"));

        return jwtService.generateToken(user);
    }

    public String refreshAccessToken(String refreshTokenString) {
        RefreshToken token = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh Token not found"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh Token invalid");
        }

        // Find user (hier Beispiel via userId)
        AppUser user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Erstelle neues Access Token (und ggf. neues Refresh Token)
        return jwtService.generateToken(user); // Passe an deine JWT-Service Methode an
    }
}
