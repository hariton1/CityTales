
package group_05.ase.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final SupabaseService supabaseService;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        String supabaseId = supabaseService.createUser(request.getEmail(), request.getPassword());

        AppUser user = AppUser.builder()
                .email(request.getEmail())
                .supabaseId(UUID.fromString(supabaseId))
                .build();
        userRepository.save(user);

        return jwtService.generateToken(user);
    }

    public String login(LoginRequest request) {
        String supabaseToken = supabaseService.loginUser(request.getEmail(), request.getPassword());

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User nicht in App DB gefunden"));

        return jwtService.generateToken(user);
    }
}
