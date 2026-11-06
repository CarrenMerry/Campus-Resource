package com.campusbooking.service;

import com.campusbooking.dto.NotificationResponseDto;
import com.campusbooking.entity.User;

import java.util.List;

public interface NotificationService {

    List<NotificationResponseDto> getUserNotifications(Long userId);

    long getUnreadCount(Long userId);

    NotificationResponseDto markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void sendNotification(User user, String title, String message, String type, Long relatedBookingId);

    void sendNotificationToAdmins(String title, String message, String type, Long relatedBookingId);
}
