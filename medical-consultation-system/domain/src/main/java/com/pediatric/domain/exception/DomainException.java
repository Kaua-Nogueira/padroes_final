package com.pediatric.domain.exception;

/**
 * Base exception for all domain-related errors.
 * Pure Java exception - no framework dependencies.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
