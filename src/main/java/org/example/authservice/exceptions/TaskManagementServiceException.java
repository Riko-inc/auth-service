package org.example.authservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TaskManagementServiceException extends RuntimeException {
    private final Map<String, Object> params = new HashMap<>();

    public TaskManagementServiceException(String message, String additionalProp) {
        super(message);
        this.params.put("Additional properties", additionalProp);
    }

    public TaskManagementServiceException(String message) {
        super(message);
    }
}
