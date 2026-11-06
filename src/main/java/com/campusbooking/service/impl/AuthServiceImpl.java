package com.campusbooking.service.impl;

import com.campusbooking.dto.AuthResponse;
import com.campusbooking.dto.LoginRequest;
import com.campusbooking.dto.RegisterRequest;
import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.entity.Faculty;
import com.campusbooking.entity.Student;
import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.InvalidUserException;
import com.campusbooking.exception.UnauthorizedOperationException;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.AuthService;
import com.campusbooking.service.TokenRevocationService;
import com.campusbooking.util.JwtUtil;
import com.campusbooking.util.PasswordUtil;
import com.campusbooking.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService.
 * Handles credential verification, user registration, role enforcement, and token revocation.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final TokenRevocationService tokenRevocationService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordUtil passwordUtil,
                           JwtUtil jwtUtil,
                           TokenRevocationService tokenRevocationService) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || !ValidationUtil.isValidEmail(request.getEmail())) {
            throw new InvalidUserException("Invalid email format.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new InvalidUserException("Password cannot be empty.");
        }

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new InvalidUserException("Invalid email or password."));

        if (!user.isActive()) {
            throw new InvalidUserException("Your account has been deactivated. Please contact the administrator.");
        }

        if (!passwordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidUserException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, UserResponseDto.fromEntity(user));
    }

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequest request) {
        // 1. Basic field validations
        if (request.getName() == null || request.getName().trim().length() < 2 || request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Full name must be between 2 and 100 characters.");
        }

        if (request.getEmail() == null || !ValidationUtil.isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("A valid institutional email address is required.");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (request.getDepartment() == null || request.getDepartment().trim().isEmpty()) {
            throw new IllegalArgumentException("Academic department is required.");
        }

        // 2. Email uniqueness check
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with email " + normalizedEmail + " already exists.");
        }

        // 3. Prevent public administrator self-registration
        if (request.getRole() == null) {
            throw new IllegalArgumentException("User role is required (STUDENT or FACULTY).");
        }

        if (request.getRole() == UserRole.ADMIN) {
            throw new UnauthorizedOperationException("Administrator accounts cannot be created via public registration.");
        }

        // 4. Role-specific validation rules
        User newUser;
        String hashedPassword = passwordUtil.hashPassword(request.getPassword());

        if (request.getRole() == UserRole.STUDENT) {
            if (request.getStudentRollNumber() == null || request.getStudentRollNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Student roll number is required for student registration.");
            }
            if (request.getStudentRollNumber().trim().length() > 50) {
                throw new IllegalArgumentException("Student roll number cannot exceed 50 characters.");
            }
            if (request.getSemester() == null || request.getSemester() < 1 || request.getSemester() > 8) {
                throw new IllegalArgumentException("Valid current semester (between 1 and 8) is required for student registration.");
            }

            Student student = new Student(
                    request.getName().trim(),
                    normalizedEmail,
                    hashedPassword,
                    request.getPhone() != null ? request.getPhone().trim() : null,
                    request.getDepartment().trim(),
                    request.getStudentRollNumber().trim().toUpperCase(),
                    request.getSemester()
            );
            newUser = student;

        } else if (request.getRole() == UserRole.FACULTY) {
            if (request.getEmployeeId() == null || request.getEmployeeId().trim().isEmpty()) {
                throw new IllegalArgumentException("Employee ID is required for faculty registration.");
            }
            if (request.getEmployeeId().trim().length() > 50) {
                throw new IllegalArgumentException("Employee ID cannot exceed 50 characters.");
            }
            if (request.getDesignation() == null || request.getDesignation().trim().isEmpty()) {
                throw new IllegalArgumentException("Designation is required for faculty registration.");
            }
            if (request.getDesignation().trim().length() > 100) {
                throw new IllegalArgumentException("Designation cannot exceed 100 characters.");
            }

            Faculty faculty = new Faculty(
                    request.getName().trim(),
                    normalizedEmail,
                    hashedPassword,
                    request.getPhone() != null ? request.getPhone().trim() : null,
                    request.getDepartment().trim(),
                    request.getEmployeeId().trim().toUpperCase(),
                    request.getDesignation().trim()
            );
            newUser = faculty;

        } else {
            throw new IllegalArgumentException("Unsupported registration role: " + request.getRole());
        }

        User savedUser = userRepository.save(newUser);
        return UserResponseDto.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new InvalidUserException("Invalid or expired session token.");
        }

        if (tokenRevocationService.isTokenRevoked(token)) {
            throw new InvalidUserException("Session has been logged out. Please log in again.");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidUserException("User associated with session not found."));

        if (!user.isActive()) {
            throw new InvalidUserException("Your account has been deactivated. Access denied.");
        }

        return UserResponseDto.fromEntity(user);
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            tokenRevocationService.revokeToken(token);
        }
    }
}
