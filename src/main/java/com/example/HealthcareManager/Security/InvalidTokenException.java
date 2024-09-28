package com.example.HealthcareManager.Security;

public class InvalidTokenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message) {
        super(message); 
    }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause); 
    }
}
