package com.campusbooking.util;

import com.campusbooking.dto.BookingRequest;
import com.campusbooking.entity.User;
import com.campusbooking.exception.BookingConflictException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Utility class offering centralized validation routines.
 * Concepts: Method Overloading, Java Date/Time validation, Defensive programming.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Validates an email address.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a phone number.
     */
    public static boolean isValidPhone(String phone) {
        return phone == null || phone.trim().isEmpty() || PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Overloaded method: Validates booking dates and times.
     */
    public static void validateBookingSchedule(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date == null) {
            throw new IllegalArgumentException("Booking date is required.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time (" + startTime + ") must be before end time (" + endTime + ").");
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes < 30) {
            throw new IllegalArgumentException("Reservation duration must be at least 30 minutes.");
        }

        // Campus operation window: e.g. 07:00 to 22:00
        if (startTime.isBefore(LocalTime.of(7, 0)) || endTime.isAfter(LocalTime.of(22, 0))) {
            throw new IllegalArgumentException("Campus resources may only be booked between 07:00 AM and 10:00 PM.");
        }
    }

    /**
     * Overloaded method: Validates booking schedule and checks against user tier limits polymorphically.
     * Demonstrates Method Overloading and Polymorphism.
     */
    public static void validateBookingSchedule(BookingRequest request, User user) {
        validateBookingSchedule(request.getBookingDate(), request.getStartTime(), request.getEndTime());

        if (user != null) {
            long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
            double hours = minutes / 60.0;
            int maxAllowedHours = user.getMaxBookingHoursPerDay();

            if (hours > maxAllowedHours) {
                throw new BookingConflictException(String.format(
                        "Reservation length of %.1f hours exceeds your role limit of %d hours per session.",
                        hours, maxAllowedHours
                ));
            }
        }
    }
}
