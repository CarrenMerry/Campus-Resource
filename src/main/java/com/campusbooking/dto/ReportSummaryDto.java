package com.campusbooking.dto;

import java.util.List;
import java.util.Map;

/**
 * Reporting DTO containing aggregated operational intelligence.
 * Generated using Java Streams, Lambdas, and Collections framework.
 */
public class ReportSummaryDto {

    // General counters
    private long totalBookings;
    private long pendingBookings;
    private long approvedBookings;
    private long rejectedBookings;
    private long cancelledBookings;
    private long completedBookings;

    // Resource counters
    private long totalResources;
    private long availableResources;
    private long maintenanceResources;

    // User counters
    private long totalUsers;
    private long studentCount;
    private long facultyCount;
    private long adminCount;

    // Aggregations calculated via Java Streams & Map
    private Map<String, Long> statusDistribution;
    private Map<String, Long> bookingsByCategory;
    private List<ResourceUsageItem> topBookedResources;

    public ReportSummaryDto() {}

    public static class ResourceUsageItem {
        private Long resourceId;
        private String resourceName;
        private String categoryName;
        private long bookingCount;
        private double totalHours;

        public ResourceUsageItem() {}

        public ResourceUsageItem(Long resourceId, String resourceName, String categoryName, long bookingCount, double totalHours) {
            this.resourceId = resourceId;
            this.resourceName = resourceName;
            this.categoryName = categoryName;
            this.bookingCount = bookingCount;
            this.totalHours = totalHours;
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

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public long getBookingCount() {
            return bookingCount;
        }

        public void setBookingCount(long bookingCount) {
            this.bookingCount = bookingCount;
        }

        public double getTotalHours() {
            return totalHours;
        }

        public void setTotalHours(double totalHours) {
            this.totalHours = totalHours;
        }
    }

    // Getters and Setters
    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getApprovedBookings() {
        return approvedBookings;
    }

    public void setApprovedBookings(long approvedBookings) {
        this.approvedBookings = approvedBookings;
    }

    public long getRejectedBookings() {
        return rejectedBookings;
    }

    public void setRejectedBookings(long rejectedBookings) {
        this.rejectedBookings = rejectedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public long getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(long totalResources) {
        this.totalResources = totalResources;
    }

    public long getAvailableResources() {
        return availableResources;
    }

    public void setAvailableResources(long availableResources) {
        this.availableResources = availableResources;
    }

    public long getMaintenanceResources() {
        return maintenanceResources;
    }

    public void setMaintenanceResources(long maintenanceResources) {
        this.maintenanceResources = maintenanceResources;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(long studentCount) {
        this.studentCount = studentCount;
    }

    public long getFacultyCount() {
        return facultyCount;
    }

    public void setFacultyCount(long facultyCount) {
        this.facultyCount = facultyCount;
    }

    public long getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(long adminCount) {
        this.adminCount = adminCount;
    }

    public Map<String, Long> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Long> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public Map<String, Long> getBookingsByCategory() {
        return bookingsByCategory;
    }

    public void setBookingsByCategory(Map<String, Long> bookingsByCategory) {
        this.bookingsByCategory = bookingsByCategory;
    }

    public List<ResourceUsageItem> getTopBookedResources() {
        return topBookedResources;
    }

    public void setTopBookedResources(List<ResourceUsageItem> topBookedResources) {
        this.topBookedResources = topBookedResources;
    }
}
