package org.example.authservice.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class GenericEventMessage<T> implements Serializable {
    private EventType eventType;
    private T payload;
    private Instant occurredAt = Instant.now();

    public GenericEventMessage(EventType eventType, T payload) {
        this.eventType = eventType;
        this.payload = payload;
    }
}
