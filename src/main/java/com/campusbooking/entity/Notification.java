package com.campusbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing an institutional in-app notification.
 * Alerts users on booking state transitions (submitted, approved, rejected, cancelled, checked-in).
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_read", columnList = "is_read")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "type", nullable = false, length = 30)
    private String type = "INFO"; // INFO, SUCCESS, WARNING, DANGER

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "related_booking_id")
    private Long relatedBookingId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.type = "INFO";
    }

    public Notification(User user, String title, String message, String type, Long relatedBookingId) {
        this();
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : "INFO";
        this.relatedBookingId = relatedBookingId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
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
