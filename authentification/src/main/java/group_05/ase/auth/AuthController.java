package group_05.ase.auth;

import group_05.ase.auth.LoginRequest;
import group_05.ase.auth.RegisterRequest;
import group_05.ase.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String jwt = authService.register(request);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String jwt = authService.login(request);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        // request.getRefreshToken()
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(new JwtResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping("/delete/{email}")
//    public ResponseEntity<String> delete(@PathVariable String email) {
//        authService.deleteAccount(email);
//        return ResponseEntity.ok("Account deleted.");
//    }
}
