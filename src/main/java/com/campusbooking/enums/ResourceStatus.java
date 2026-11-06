package com.campusbooking.enums;

/**
 * Enumeration representing the operational condition of a campus resource.
 */
public enum ResourceStatus {
    AVAILABLE("Available for Booking", "success"),
    MAINTENANCE("Under Maintenance", "warning"),
    INACTIVE("Inactive / Decommissioned", "danger");

    private final String description;
    private final String badgeColor;

    ResourceStatus(String description, String badgeColor) {
        this.description = description;
        this.badgeColor = badgeColor;
    }

    public String getDescription() {
        return description;
    }

    public String getBadgeColor() {
        return badgeColor;
    }

    public boolean isBookable() {
        return this == AVAILABLE;
    }
}
