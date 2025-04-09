package org.example.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends TaskManagementServiceException {

    public EntityNotFoundException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
