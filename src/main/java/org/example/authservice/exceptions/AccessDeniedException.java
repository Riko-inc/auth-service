package org.example.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends TaskManagementServiceException {

    public AccessDeniedException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
