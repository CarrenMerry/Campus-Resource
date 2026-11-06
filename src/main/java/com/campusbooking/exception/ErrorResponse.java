package com.campusbooking.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error payload returned to clients on application exceptions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success = false;
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.success = false;
    }

    public ErrorResponse(int status, String error, String message) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this(status, error, message);
        this.validationErrors = validationErrors;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
