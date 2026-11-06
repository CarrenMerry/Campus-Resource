package com.campusbooking.util;

import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.InvalidUserException;
import com.campusbooking.exception.UnauthorizedOperationException;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.TokenRevocationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Helper to extract and verify the authenticated principal from incoming HTTP requests.
 */
@Component
public class SecurityUtil {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenRevocationService tokenRevocationService;

    public SecurityUtil(JwtUtil jwtUtil, UserRepository userRepository, TokenRevocationService tokenRevocationService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRevocationService = tokenRevocationService;
    }

    public User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidUserException("Authorization token missing or malformed.");
        }

        String token = authHeader.substring(7).trim();
        if (!jwtUtil.isTokenValid(token)) {
            throw new InvalidUserException("Session expired or invalid token. Please log in again.");
        }

        if (tokenRevocationService.isTokenRevoked(token)) {
            throw new InvalidUserException("Session has been logged out. Please log in again.");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidUserException("User not found for token."));

        if (!user.isActive()) {
            throw new InvalidUserException("Your account has been deactivated. Access denied.");
        }

        return user;
    }

    public void requireAdmin(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedOperationException("Administrator privileges required for this action.");
        }
    }
}
