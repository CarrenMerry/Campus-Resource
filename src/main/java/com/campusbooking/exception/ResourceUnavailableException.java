package com.campusbooking.exception;

/**
 * Custom Exception thrown when a requested resource is under maintenance or inactive.
 * Maps to HTTP 400 Bad Request.
 */
public class ResourceUnavailableException extends RuntimeException {
    public ResourceUnavailableException(String message) {
        super(message);
    }
}
