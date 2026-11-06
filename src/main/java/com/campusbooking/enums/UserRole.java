package com.campusbooking.enums;

/**
 * Enumeration representing user authorization roles in the Campus Resource Booking System.
 * Concept: Enums provide type safety, eliminating error-prone hard-coded strings.
 */
public enum UserRole {
    STUDENT("Student"),
    FACULTY("Faculty"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
