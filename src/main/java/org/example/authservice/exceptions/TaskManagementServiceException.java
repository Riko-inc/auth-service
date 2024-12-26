package org.example.authservice.exceptions;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
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
