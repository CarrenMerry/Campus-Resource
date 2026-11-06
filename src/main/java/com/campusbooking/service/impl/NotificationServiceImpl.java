package com.campusbooking.service.impl;

import com.campusbooking.dto.NotificationResponseDto;
import com.campusbooking.entity.Notification;
import com.campusbooking.entity.User;
import com.campusbooking.enums.UserRole;
import com.campusbooking.exception.ResourceNotFoundException;
import com.campusbooking.repository.NotificationRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cannot update notification belonging to another user.");
        }

        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @Transactional
    public void sendNotification(User user, String title, String message, String type, Long relatedBookingId) {
        if (user == null) return;
        Notification notification = new Notification(user, title, message, type, relatedBookingId);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void sendNotificationToAdmins(String title, String message, String type, Long relatedBookingId) {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        for (User admin : admins) {
            Notification notification = new Notification(admin, title, message, type, relatedBookingId);
            notificationRepository.save(notification);
        }
    }
}
