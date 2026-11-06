package com.campusbooking.controller;

import com.campusbooking.dto.ApiResponse;
import com.campusbooking.dto.NotificationResponseDto;
import com.campusbooking.entity.User;
import com.campusbooking.service.NotificationService;
import com.campusbooking.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    public NotificationController(NotificationService notificationService, SecurityUtil securityUtil) {
        this.notificationService = notificationService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserNotifications(HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        List<NotificationResponseDto> notifications = notificationService.getUserNotifications(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Notifications retrieved", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        long count = notificationService.getUnreadCount(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Unread count retrieved", Collections.singletonMap("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markAsRead(
            @PathVariable("id") Long id,
            HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        NotificationResponseDto updated = notificationService.markAsRead(id, user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", updated));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(HttpServletRequest request) {
        User user = securityUtil.getAuthenticatedUser(request);
        notificationService.markAllAsRead(user.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }
}
