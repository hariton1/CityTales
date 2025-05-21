package group_05.ase.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;

    // Konstruktor für SecretKey als String
    public JwtService(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Standardkonstruktor für Spring (nimmt einen Default-Wert)
    public JwtService() {
        this("superlongsecretkeythatissafeforHS256_123456!");
    }

//    public JwtService(@Value("${jwt.secret}") String secret) {
//        this.key = Keys.hmacShaKeyFor(secret.getBytes());
//    }

    public String generateToken(AppUser user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("supabaseId", user.getSupabaseId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
