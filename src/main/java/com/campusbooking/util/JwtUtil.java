package com.campusbooking.util;

import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility for issuing and verifying JSON Web Tokens (JWT).
 */
@Component
public class JwtUtil {

    // Default key if not supplied via properties
    private static final String DEFAULT_SECRET = "campusResourceBookingSystemSecureSecretKey2026MCAProjectAcademicKey1234567890";
    private final Key key;
    private final long expirationMillis = 24 * 60 * 60 * 1000; // 24 hours

    public JwtUtil(@Value("${app.jwt.secret:" + DEFAULT_SECRET + "}") String secret) {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // Ensure 256 bits minimum for HS256
            secret = String.format("%-32s", secret).replace(' ', '0');
            keyBytes = secret.getBytes();
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().name());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object id = claims.get("userId");
        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        return null;
    }

    public UserRole extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleStr = (String) claims.get("role");
        if (roleStr != null) {
            return UserRole.valueOf(roleStr);
        }
        return null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
