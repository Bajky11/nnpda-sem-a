package nndpa.sem_a.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtAuthenticationService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;  // Tajný klíč pro podpis JWT

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;  // Doba platnosti tokenu

    /**
     * Extrahuje uživatelské jméno (subject) z tokenu.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Obecná metoda pro extrakci claimů z tokenu.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Vygeneruje token pro uživatele na základě uživatelských detailů.
     */
    public String generateToken(String username) {
        return buildToken(new HashMap<>(), username, jwtExpiration);
    }

    /**
     * Kontroluje, zda je token platný pro zadané uživatelské detaily.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Získá datum vypršení platnosti z tokenu.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Ověří, zda token vypršel.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Vytváří a podepisuje nový token s dodatečnými claimy a uživatelským jménem.
     */
    private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
        System.out.println("Signing key: " + secretKey);
        return Jwts.builder()
                .setClaims(extraClaims)                 // Nastaví extra claimy
                .setSubject(username)                   // Nastaví subject (uživatelské jméno)
                .setIssuedAt(new Date())                // Nastaví datum vytvoření
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // Nastaví datum expirace
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Podepíše token
                .compact();
    }

    /**
     * Extrahuje všechny claimy z tokenu.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())  // Nastaví klíč pro ověření podpisu
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Získá klíč pro podpis tokenu na základě tajného klíče.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Dekóduje tajný klíč
        return Keys.hmacShaKeyFor(keyBytes);  // Vytvoří HMAC SHA klíč
    }
}
