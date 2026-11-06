package com.campusbooking.exception;

/**
 * Custom Exception thrown when an overlapping reservation or double-booking conflict occurs.
 * Maps to HTTP 409 Conflict.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }

    public BookingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
