package com.campusbooking.dto;

import com.campusbooking.entity.Booking;
import com.campusbooking.enums.BookingStatus;
import com.campusbooking.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Detailed DTO for booking reservations.
 * Concept: DTO design pattern decouples JPA entity graph from API serialization.
 */
public class BookingResponseDto {

    private Long bookingId;

    // User details
    private Long userId;
    private String userName;
    private String userEmail;
    private UserRole userRole;
    private String userDepartment;

    // Resource details
    private Long resourceId;
    private String resourceName;
    private Long categoryId;
    private String categoryName;
    private String location;
    private Integer capacity;
    private String resourceImageUrl;

    // Reservation details
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private double durationHours;
    private String purpose;
    private BookingStatus status;
    private String statusDescription;
    private String badgeColor;
    private String adminRemark;
    private Integer attendeesCount;
    private boolean cancellable;
    private String checkinCode;
    private boolean checkedIn;
    private LocalDateTime checkInTime;

    private LocalDateTime createdAt;

    public BookingResponseDto() {}

    public static BookingResponseDto fromEntity(Booking booking) {
        if (booking == null) return null;
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getBookingId());

        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getUserId());
            dto.setUserName(booking.getUser().getName());
            dto.setUserEmail(booking.getUser().getEmail());
            dto.setUserRole(booking.getUser().getRole());
            dto.setUserDepartment(booking.getUser().getDepartment());
        }

        if (booking.getResource() != null) {
            dto.setResourceId(booking.getResource().getResourceId());
            dto.setResourceName(booking.getResource().getResourceName());
            dto.setLocation(booking.getResource().getLocation());
            dto.setCapacity(booking.getResource().getCapacity());
            dto.setResourceImageUrl(booking.getResource().getImageUrl());

            if (booking.getResource().getCategory() != null) {
                dto.setCategoryId(booking.getResource().getCategory().getCategoryId());
                dto.setCategoryName(booking.getResource().getCategory().getCategoryName());
            }
        }

        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setDurationHours(booking.getDurationInHours());
        dto.setPurpose(booking.getPurpose());
        dto.setStatus(booking.getStatus());
        dto.setStatusDescription(booking.getStatus() != null ? booking.getStatus().getDescription() : "");
        dto.setBadgeColor(booking.getStatus() != null ? booking.getStatus().getBadgeColor() : "secondary");
        dto.setAdminRemark(booking.getAdminRemark());
        dto.setAttendeesCount(booking.getAttendeesCount());
        dto.setCancellable(booking.getStatus() != null && booking.getStatus().isCancellable());
        dto.setCheckinCode(booking.getCheckinCode());
        dto.setCheckedIn(booking.isCheckedIn());
        dto.setCheckInTime(booking.getCheckInTime());
        dto.setCreatedAt(booking.getCreatedAt());

        return dto;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getResourceImageUrl() {
        return resourceImageUrl;
    }

    public void setResourceImageUrl(String resourceImageUrl) {
        this.resourceImageUrl = resourceImageUrl;
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

    public double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(double durationHours) {
        this.durationHours = durationHours;
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

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(String badgeColor) {
        this.badgeColor = badgeColor;
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

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
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
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
}
