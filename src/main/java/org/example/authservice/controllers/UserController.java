package org.example.authservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.responses.GetUserResponse;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.services.AuthenticationService;
import org.example.authservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "Работа с пользователями и аккаунтами")
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    //TODO: Изменить на получение пользователей текущего пространства
    @Operation(summary = "Получить список всех пользователей")
    @GetMapping()
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<List<GetUserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Проверить, что пользователь с заданным email существует")
    @GetMapping("/check-email")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Boolean> validateEmail(@Valid @RequestHeader("Email") String email) {
        return ResponseEntity.ok(authenticationService.checkEmailExists(email));
    }

    @Operation(summary = "Проверить, существует ли пользователь с заданным id")
    @GetMapping("/check-id")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Boolean> checkUserId(@RequestHeader("Id") Long userId) {
        return ResponseEntity.ok(authenticationService.checkUserId(userId));
    }

    @Operation(summary = "Получить email текущего пользователя")
    @GetMapping("/email")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> extractEmail(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(authenticationService.extractEmail(user));
    }

    @Operation(summary = "Получить роль текущего пользователя")
    @GetMapping("/role")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<String> getRole(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(authenticationService.extractRole(user));
    }
}
