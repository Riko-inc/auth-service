package org.example.authservice.domain.events;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserCreatedEvent {
    private Long userId;
    private String email;
}
