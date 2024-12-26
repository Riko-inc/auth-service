package org.example.authservice.exceptions;

public class AccessDeniedException extends TaskManagementServiceException {

    public AccessDeniedException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
