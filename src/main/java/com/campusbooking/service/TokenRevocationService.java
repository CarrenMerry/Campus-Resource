package com.campusbooking.service;

import com.campusbooking.entity.RevokedToken;
import com.campusbooking.repository.RevokedTokenRepository;
import com.campusbooking.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing server-side JWT revocation / token blacklisting.
 * Provides immediate server-side logout invalidation with dual-layer storage:
 * 1. High-throughput in-memory ConcurrentHashMap for O(1) fast-path checks.
 * 2. Persistent JPA database repository for survivability across application restarts.
 * 3. Automatic periodic scheduled cleanup of naturally expired revoked tokens.
 */
@Service
public class TokenRevocationService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtUtil jwtUtil;

    // Cache of tokenHash -> expiration epoch milliseconds
    private final Map<String, Long> revokedTokenCache = new ConcurrentHashMap<>();

    public TokenRevocationService(RevokedTokenRepository revokedTokenRepository, JwtUtil jwtUtil) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Warms up in-memory cache with non-expired revoked tokens from database on startup.
     */
    @PostConstruct
    public void initCache() {
        try {
            List<RevokedToken> activeRevocations = revokedTokenRepository.findByExpiryDateAfter(LocalDateTime.now());
            for (RevokedToken token : activeRevocations) {
                long expiryMillis = token.getExpiryDate()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                revokedTokenCache.put(token.getTokenHash(), expiryMillis);
            }
        } catch (Exception e) {
            // Table may not yet be initialized or database warming up
        }
    }

    /**
     * Revokes an active JWT token immediately.
     */
    @Transactional
    public void revokeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            Date expiration = jwtUtil.extractExpiration(token);
            if (expiration == null || expiration.before(new Date())) {
                // Token is already expired naturally, no need to store
                return;
            }

            String tokenHash = hashToken(token);
            long expiryMillis = expiration.getTime();

            // 1. Store in memory cache
            revokedTokenCache.put(tokenHash, expiryMillis);

            // 2. Persist in database
            LocalDateTime expiryDateTime = LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
            if (!revokedTokenRepository.existsByTokenHash(tokenHash)) {
                RevokedToken revokedToken = new RevokedToken(tokenHash, expiryDateTime);
                revokedTokenRepository.save(revokedToken);
            }
        } catch (Exception e) {
            // If token is invalid or malformed, it won't be accepted anyway
        }
    }

    /**
     * Checks whether a JWT token has been revoked / logged out.
     */
    public boolean isTokenRevoked(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        String tokenHash = hashToken(token);
        Long expiryMillis = revokedTokenCache.get(tokenHash);

        if (expiryMillis != null) {
            if (System.currentTimeMillis() < expiryMillis) {
                return true;
            } else {
                // Expired naturally, remove from cache
                revokedTokenCache.remove(tokenHash);
                return false;
            }
        }

        // Fallback check in database
        try {
            return revokedTokenRepository.existsByTokenHash(tokenHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Scheduled cleanup job running every hour to purge expired tokens.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        revokedTokenCache.entrySet().removeIf(entry -> entry.getValue() <= now);

        try {
            revokedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        } catch (Exception ignored) {
        }
    }

    /**
     * Computes SHA-256 hexadecimal hash of the token string.
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 cryptographic algorithm unavailable", e);
        }
    }
}

