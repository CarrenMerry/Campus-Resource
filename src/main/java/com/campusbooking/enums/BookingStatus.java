package com.campusbooking.enums;

/**
 * Enumeration representing the life-cycle states of a resource reservation.
 * Concept: Enums with helper methods for state machine transitions.
 */
public enum BookingStatus {
    PENDING("Pending Approval", "warning"),
    APPROVED("Approved", "success"),
    REJECTED("Rejected", "danger"),
    CANCELLED("Cancelled", "secondary"),
    COMPLETED("Completed", "info");

    private final String description;
    private final String badgeColor;

    BookingStatus(String description, String badgeColor) {
        this.description = description;
        this.badgeColor = badgeColor;
    }

    public String getDescription() {
        return description;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    /**
     * Determines whether this booking occupies a resource time slot.
     */
    public boolean occupiesSlot() {
        return this == PENDING || this == APPROVED;
    }

    /**
     * Determines whether a user is allowed to cancel this booking.
     */
    public boolean isCancellable() {
        return this == PENDING || this == APPROVED;
    }
}
