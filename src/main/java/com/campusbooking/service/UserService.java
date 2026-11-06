package com.campusbooking.service;

import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.enums.UserRole;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> searchUsers(UserRole role, String query);
    UserResponseDto getUserById(Long userId);
    UserResponseDto toggleUserStatus(Long userId, boolean active);
    UserResponseDto updateUserProfile(Long userId, String name, String phone, String department);
    void changePassword(Long userId, String currentPassword, String newPassword);
}
