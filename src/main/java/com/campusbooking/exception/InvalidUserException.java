package com.campusbooking.exception;

/**
 * Custom Exception thrown when authentication fails or user credentials/status are invalid.
 * Maps to HTTP 401 Unauthorized or 400 Bad Request.
 */
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }
}
