package org.example.authservice.domain.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.authservice.domain.entities.UserEntity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ на получение пользователя")
public class GetUserResponse {
    @Schema(description = "Id пользователя", example = "12")
    private Long id;

    @Schema(description = "Адрес электронной почты", example = "john@gmail.com")
    @Email
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    private UserEntity.Role role;

    @Schema(type = "string", description = "Дата и время регистрации", example = "14-05-2024 20:50")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime registrationDateTime;
}