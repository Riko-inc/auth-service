package org.example.authservice.utils;

import org.example.authservice.domain.events.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublishStringEvent {
    EventType eventType();
    String payloadExpression();
    String topic();
}