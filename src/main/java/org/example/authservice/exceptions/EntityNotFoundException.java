package org.example.authservice.exceptions;

public class EntityNotFoundException extends TaskManagementServiceException {

    public EntityNotFoundException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
