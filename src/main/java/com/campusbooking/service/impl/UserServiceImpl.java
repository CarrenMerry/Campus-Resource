package com.campusbooking.service.impl;

import com.campusbooking.dto.UserResponseDto;
import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.InvalidUserException;
import com.campusbooking.exception.ResourceNotFoundException;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.UserService;
import com.campusbooking.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing user profiles and administration.
 * Concepts: Streams, Lambda Expressions, Layered Architecture, Exception Handling.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        // Concept: Java Streams to map entity collection to DTO collection
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> searchUsers(UserRole role, String query) {
        String searchQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return userRepository.searchUsers(role, searchQuery).stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserResponseDto.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponseDto toggleUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Use Manageable interface methods
        if (active) {
            user.activate();
        } else {
            user.deactivate();
        }

        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserProfile(Long userId, String name, String phone, String department) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        if (phone != null) {
            user.setPhone(phone.trim());
        }
        if (department != null) {
            user.setDepartment(department.trim());
        }

        User updated = userRepository.save(user);
        return UserResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordUtil.verifyPassword(currentPassword, user.getPassword())) {
            throw new InvalidUserException("Current password does not match our records.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        user.setPassword(passwordUtil.hashPassword(newPassword));
        userRepository.save(user);
    }
}
