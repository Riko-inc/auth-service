package org.example.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserAlreadyExistException extends TaskManagementServiceException {

    public UserAlreadyExistException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
