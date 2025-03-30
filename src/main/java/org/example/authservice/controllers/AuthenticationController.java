package org.example.authservice.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.dto.requests.UserRefreshTokenRequest;
import org.example.authservice.domain.dto.requests.UserSignInRequest;
import org.example.authservice.domain.dto.requests.UserSignUpRequest;
import org.example.authservice.domain.dto.responses.UserDetailResponse;
import org.example.authservice.domain.dto.responses.UserJwtAuthenticationResponse;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<UserJwtAuthenticationResponse> register(@RequestBody @Valid UserSignUpRequest request) {
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Аутентифицировать зарегистрированного пользователя и получить JWT токены")
    @PostMapping("/authenticate")
    public ResponseEntity<UserJwtAuthenticationResponse> authenticate(@RequestBody @Valid UserSignInRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Выпустить новую пару токенов с помощью refresh токена")
    @PostMapping("/refresh-token")
    public ResponseEntity<UserJwtAuthenticationResponse> refreshToken(@RequestBody @Valid UserRefreshTokenRequest request) {
        return ResponseEntity.ok(service.refreshToken(request));
    }

    @Operation(summary = "Проверить, что пользователь с заданным email существует")
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> validateEmail(@Valid @RequestHeader("Email") String email) {
        return ResponseEntity.ok(service.checkEmailExists(email));
    }

    @Operation(summary = "Получить email текущего пользователя")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/extract-email")
    public ResponseEntity<String> extractEmail(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(service.extractEmail(user));
    }

    @Operation(summary = "Получить роль текущего пользователя")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/role")
    public ResponseEntity<String> getRole(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(service.extractRole(user));
    }

    @Operation(summary = "Получить UserDetails из токена пользователя")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/details")
    public ResponseEntity<UserDetailResponse> getUserDetails(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(service.getUserDetails(user));
    }
}