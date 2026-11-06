package com.campusbooking.service;

import com.campusbooking.dto.NotificationResponseDto;
import com.campusbooking.entity.Admin;
import com.campusbooking.entity.Notification;
import com.campusbooking.entity.Student;
import com.campusbooking.enums.UserRole;
import com.campusbooking.repository.NotificationRepository;
import com.campusbooking.repository.UserRepository;
import com.campusbooking.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Student testStudent;
    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testStudent = new Student("Alice", "alice@campus.edu", "pass", "1234567890", "MCA", "MCA01", 3);
        testStudent.setUserId(10L);

        testAdmin = new Admin("Admin Carol", "carol@campus.edu", "pass", "1234567892", "IT", "ADM01", true);
        testAdmin.setUserId(20L);
    }

    @Test
    @DisplayName("Notification: Retrieve user notifications")
    void testGetUserNotifications() {
        Notification n1 = new Notification(testStudent, "Title 1", "Msg 1", "INFO", 1L);
        Notification n2 = new Notification(testStudent, "Title 2", "Msg 2", "SUCCESS", 2L);
        when(notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(10L)).thenReturn(Arrays.asList(n1, n2));

        List<NotificationResponseDto> list = notificationService.getUserNotifications(10L);

        assertEquals(2, list.size());
        assertEquals("Title 1", list.get(0).getTitle());
        assertEquals("SUCCESS", list.get(1).getType());
    }

    @Test
    @DisplayName("Notification: Get unread count")
    void testGetUnreadCount() {
        when(notificationRepository.countByUser_UserIdAndIsReadFalse(10L)).thenReturn(3L);

        long count = notificationService.getUnreadCount(10L);

        assertEquals(3L, count);
    }

    @Test
    @DisplayName("Notification: Mark as read")
    void testMarkAsRead() {
        Notification n1 = new Notification(testStudent, "Title 1", "Msg 1", "INFO", 1L);
        n1.setNotificationId(50L);
        when(notificationRepository.findById(50L)).thenReturn(Optional.of(n1));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponseDto result = notificationService.markAsRead(50L, 10L);

        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(n1);
    }

    @Test
    @DisplayName("Notification: Send notification to admins broadcast")
    void testSendNotificationToAdmins() {
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(Arrays.asList(testAdmin));

        notificationService.sendNotificationToAdmins("Alert", "System notice", "WARNING", null);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
