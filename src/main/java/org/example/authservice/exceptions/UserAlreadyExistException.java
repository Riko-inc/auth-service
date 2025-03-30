package org.example.authservice.exceptions;

public class UserAlreadyExistException extends TaskManagementServiceException {

    public UserAlreadyExistException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
