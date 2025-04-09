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
    private final AuthenticationService authenticationService;

    @Operation(summary = "Зарегистрировать пользователя и получить JWT токены")
    @PostMapping("/register")
    public ResponseEntity<UserJwtAuthenticationResponse> register(@RequestBody @Valid UserSignUpRequest request) {
        return new ResponseEntity<>(authenticationService.register(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Аутентифицировать зарегистрированного пользователя и получить JWT токены")
    @PostMapping("/authenticate")
    public ResponseEntity<UserJwtAuthenticationResponse> authenticate(@RequestBody @Valid UserSignInRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(summary = "Выпустить новую пару токенов с помощью refresh токена")
    @PostMapping("/refresh-token")
    public ResponseEntity<UserJwtAuthenticationResponse> refreshToken(@RequestBody @Valid UserRefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    @Operation(summary = "Получить UserDetails из токена пользователя")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/details")
    public ResponseEntity<UserDetailResponse> getUserDetails(@Valid @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(authenticationService.getUserDetails(user));
    }

    @Operation(summary = "Проверить JWT access токен на валидность")
    @GetMapping("/check-token")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<Boolean> checkToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authenticationService.checkToken(token));
    }
}