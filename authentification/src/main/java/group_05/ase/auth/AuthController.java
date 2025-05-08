package group_05.ase.auth;

import group_05.ase.auth.LoginRequest;
import group_05.ase.auth.RegisterRequest;
import group_05.ase.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

//    @DeleteMapping("/delete/{email}")
//    public ResponseEntity<String> delete(@PathVariable String email) {
//        authService.deleteAccount(email);
//        return ResponseEntity.ok("Account deleted.");
//    }
}
