package org.example.authservice.domain.dto.requests;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос с токеном доступа")
public class UserAccessTokenRequest {
        @Schema(description = "access token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
        @NotBlank(message = "Access токен должен быть передан в теле запроса")
        private String accessToken;
}
