package com.campusbooking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity storing revoked/blacklisted JWT tokens upon user logout.
 * Ensures persistent server-side token invalidation across sessions and restarts.
 */
@Entity
@Table(name = "revoked_tokens", indexes = {
        @Index(name = "idx_revoked_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_revoked_expiry", columnList = "expiry_date")
})
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    public RevokedToken() {
        this.revokedAt = LocalDateTime.now();
    }

    public RevokedToken(String tokenHash, LocalDateTime expiryDate) {
        this();
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
}

