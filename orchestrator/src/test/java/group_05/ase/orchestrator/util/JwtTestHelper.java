package group_05.ase.orchestrator.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Base64;

public class JwtTestHelper {

    private static final String base64Secret = "***REMOVED***";
    private static final byte[] secret = Base64.getDecoder().decode(base64Secret);

    public static String generateValidToken(String userId) {
        long now = System.currentTimeMillis();
        long expiration = now + 1000 * 60 * 60; // 1 Stunde gültig

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}

