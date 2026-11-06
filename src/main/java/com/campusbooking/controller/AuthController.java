package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.AuthResponse;
import com.campusbooking.dto.LoginRequest;
import com.campusbooking.dto.RegisterRequest;
import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling Authentication, User Registration, Session Verification, and Logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponseDto userDto = authService.register(request);
        return new ResponseEntity<>(ApiResponse.ok("Registration successful. Please log in.", userDto), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String token) {
        UserResponseDto currentUser = authService.getCurrentUser(token);
        return ResponseEntity.ok(ApiResponse.ok("Current user profile retrieved", currentUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully"));
    }
}
