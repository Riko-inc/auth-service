package org.example.authservice.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "Запрос на обновление данных пользователя")
public class UserUpdateRequest {

    @Schema(description = "Адрес электронной почты", example = "john@gmail.com")
    @Length(min = 2, message = "Адрес электронной почты должен содержать более 2 символов")
    @Email(message = "Email адрес должен быть в формате user@gmail.com")
    @NotBlank(message = "Поле email не может быть пустым")
    private String email;

    @Schema(description = "Пароль", example = "my_password123")
    @Length(min = 4, max = 255, message = "Длина пароля должна быть не менее 4 и не более 255 символов")
    private String password;
}
