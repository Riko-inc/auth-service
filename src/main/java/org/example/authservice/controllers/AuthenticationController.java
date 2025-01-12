package org.example.authservice.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.requests.UserAccessTokenRequest;
import org.example.authservice.domain.dto.requests.UserRefreshTokenRequest;
import org.example.authservice.domain.dto.requests.UserSignInRequest;
import org.example.authservice.domain.dto.requests.UserSignUpRequest;
import org.example.authservice.domain.dto.responses.UserJwtAuthenticationResponse;
import org.example.authservice.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Авторизация и аутентификация")
public class AuthenticationController {
    private final AuthenticationService service;

    @Operation(summary = "Зарегистрировать пользователя и получить JWT токены")
    @PostMapping("/register")
    public ResponseEntity<UserJwtAuthenticationResponse> register(@RequestBody @Validated UserSignUpRequest request) {
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Аутентифицировать зарегистрированного пользователя и получить JWT токены")
    @PostMapping("/authenticate")
    public ResponseEntity<UserJwtAuthenticationResponse> authenticate(@RequestBody @Validated UserSignInRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Выпустить новую пару токенов с помощью refresh токена")
    @PostMapping("/refresh-token")
    public ResponseEntity<UserJwtAuthenticationResponse> refreshToken(@RequestBody @Validated UserRefreshTokenRequest request) {
        return ResponseEntity.ok(service.refreshToken(request));
    }

    @Operation(summary = "Проверить JWT access токен на валидность")
    @GetMapping("/check-token")
    public ResponseEntity<Boolean> checkToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.checkToken(token));
    }

    @Operation(summary = "Проверить, что пользователь с заданным email существует")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> validateEmail(@RequestHeader("Email") String email) {
        return ResponseEntity.ok(service.checkEmailExists(email));
    }

    @Operation(summary = "Получить email из токена пользователя")
    @GetMapping("/extract-email")
    public ResponseEntity<String> extractEmail(@RequestHeader("Email") String token) {
        return ResponseEntity.ok(service.extractEmail(token));
    }

    @Operation(summary = "Получить роль пользователя из его токена")
    @PostMapping("/role")
    public ResponseEntity<String> getRole(@RequestBody @Validated UserAccessTokenRequest request) {
        return ResponseEntity.ok(service.getRoleByToken(request.getAccessToken()));
    }
}