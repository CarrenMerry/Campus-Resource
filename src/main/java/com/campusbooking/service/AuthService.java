package com.campusbooking.service;

import com.campusbooking.dto.AuthResponse;
import com.campusbooking.dto.LoginRequest;
import com.campusbooking.dto.RegisterRequest;
import com.campusbooking.dto.UserResponseDto;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    UserResponseDto register(RegisterRequest request);
    UserResponseDto getCurrentUser(String token);
    void logout(String token);
}
