package com.campusbooking.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Security utility for hashing and verifying passwords using BCrypt.
 * Protects user credentials and complies with academic security standards.
 */
@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hashes raw plain-text password using BCrypt salt rounds.
     */
    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Compares raw plain-text password against a hashed string.
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
