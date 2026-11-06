package com.campusbooking.dto;

import com.campusbooking.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class NotificationResponseDto {

    private Long notificationId;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private Long relatedBookingId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public NotificationResponseDto() {}

    public static NotificationResponseDto fromEntity(Notification notification) {
        if (notification == null) return null;
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setNotificationId(notification.getNotificationId());
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getUserId());
        }
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setRelatedBookingId(notification.getRelatedBookingId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getRelatedBookingId() {
        return relatedBookingId;
    }

    public void setRelatedBookingId(Long relatedBookingId) {
        this.relatedBookingId = relatedBookingId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
