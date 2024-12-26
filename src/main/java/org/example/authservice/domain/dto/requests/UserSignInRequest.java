package org.example.authservice.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Запрос на аутентификацию")
public class UserSignInRequest {

    @Schema(description = "Адрес электронной почты", example = "john@gmail.com")
    @Length(min = 2, message = "Адрес электронной почты должен содержать более 2 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@gmail.com")
    private String email;

    @Schema(description = "Пароль", example = "my_password123")
    @Length(min = 4, max = 255, message = "Длина пароля должна быть от 4 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
