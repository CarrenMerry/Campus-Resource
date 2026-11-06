package com.campusbooking.entity;

import com.campusbooking.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing an institutional campus resource reservation.
 * 
 * Concepts Demonstrated:
 * - Encapsulation: Strict private fields, validation annotations, accessors/mutators.
 * - Relational Mapping: Foreign keys to User and Resource.
 * - Java Date/Time API: LocalDate, LocalTime, LocalDateTime, Duration.
 * - Domain Methods: Self-contained overlap detection logic.
 */
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_resource_date", columnList = "resource_id, booking_date"),
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_status", columnList = "status")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Resource is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @NotNull(message = "Booking date is required")
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotBlank(message = "Booking purpose is required")
    @Size(min = 5, max = 500, message = "Purpose must be between 5 and 500 characters")
    @Column(name = "purpose", nullable = false, length = 500)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "admin_remark", length = 500)
    private String adminRemark;

    @Column(name = "attendees_count")
    private Integer attendeesCount = 1;

    @Column(name = "checkin_code", length = 64, unique = true)
    private String checkinCode;

    @Column(name = "is_checked_in", nullable = false)
    private boolean isCheckedIn = false;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Booking() {
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.isCheckedIn = false;
        this.checkinCode = generateUniqueCheckinCode();
    }

    public Booking(User user, Resource resource, LocalDate bookingDate, 
                   LocalTime startTime, LocalTime endTime, String purpose, Integer attendeesCount) {
        this();
        this.user = user;
        this.resource = resource;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.attendeesCount = attendeesCount != null ? attendeesCount : 1;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.checkinCode == null || this.checkinCode.trim().isEmpty()) {
            this.checkinCode = generateUniqueCheckinCode();
        }
    }

    private String generateUniqueCheckinCode() {
        return "CRB-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    // ==========================================
    // DOMAIN BUSINESS LOGIC
    // ==========================================

    /**
     * Calculates duration in fractional hours using Java Date/Time API.
     */
    public double getDurationInHours() {
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            return duration.toMinutes() / 60.0;
        }
        return 0.0;
    }

    /**
     * Evaluates if this booking overlaps with a requested time interval on the same date.
     * Core Double-Booking Rule:
     * existing.start_time < requested.end_time AND existing.end_time > requested.start_time
     */
    public boolean overlapsWith(LocalDate requestedDate, LocalTime requestedStart, LocalTime requestedEnd) {
        if (!this.bookingDate.equals(requestedDate)) {
            return false;
        }
        // Only active slot-occupying bookings can conflict
        if (!this.status.occupiesSlot()) {
            return false;
        }
        return this.startTime.isBefore(requestedEnd) && this.endTime.isAfter(requestedStart);
    }

    // ==========================================
    // ENCAPSULATION: GETTERS AND SETTERS
    // ==========================================

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }

    public Integer getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(Integer attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(String checkinCode) {
        this.checkinCode = checkinCode;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
