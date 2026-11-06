package com.campusbooking.exception;

/**
 * Custom Exception thrown when a booking is not found.
 * Maps to HTTP 404 Not Found.
 */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}
