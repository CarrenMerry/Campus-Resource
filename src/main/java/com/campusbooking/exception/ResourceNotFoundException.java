package com.campusbooking.exception;

/**
 * Custom Exception thrown when a resource with the specified identifier is not found.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
