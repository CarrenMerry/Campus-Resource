package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.entity.User;
import com.campusbooking.service.UserService;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for user profile self-management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SecurityUtil securityUtil;

    public UserController(UserService userService, SecurityUtil securityUtil) {
        this.userService = userService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> getProfile(HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        UserResponseDto dto = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved", dto));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        String name = body.get("name");
        String phone = body.get("phone");
        String department = body.get("department");

        UserResponseDto updated = userService.updateUserProfile(user.getUserId(), name, phone, department);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        String oldPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        userService.changePassword(user.getUserId(), oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }
}
