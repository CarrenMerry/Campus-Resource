package com.campusbooking.exception;

/**
 * Custom Exception thrown when an unauthorized user attempts an admin or restricted action.
 * Maps to HTTP 403 Forbidden.
 */
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
