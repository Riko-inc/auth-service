package org.example.authservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestParameterException extends TaskManagementServiceException {

    public InvalidRequestParameterException(String message, String additionalProp) {
        super(message, additionalProp);
    }

    public InvalidRequestParameterException(String message) {
        super(message);
    }
}
