package org.example.authservice.services;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.authservice.domain.dto.requests.UserAccessTokenRequest;
import org.example.authservice.domain.dto.requests.UserRefreshTokenRequest;
import org.example.authservice.domain.dto.requests.UserSignInRequest;
import org.example.authservice.domain.dto.requests.UserSignUpRequest;
import org.example.authservice.domain.dto.responses.UserJwtAuthenticationResponse;
import org.example.authservice.domain.entities.TokenRedisEntity;
import org.example.authservice.domain.entities.UserEntity;
import org.example.authservice.exceptions.AccessDeniedException;
import org.example.authservice.repositories.TokenRepository;
import org.example.authservice.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.StyledEditorKit;

/**
 * Contains logic for getting JWT tokens
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${application.security.jwt.expiration}")
    private long accessExpTime;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpTime;


    // TODO: remove

    @EventListener(ApplicationReadyEvent.class)
    public void adminCreator() {
        UserEntity user = userService.create(UserEntity.builder()
                .role(UserEntity.Role.ADMIN)
                .email("a@admin.com")
                .password(passwordEncoder.encode("12345678"))
                .build());

        var response = generateTokenResponse(user);

        System.out.println("Created admin user: ");
        System.out.println("email: a@admin.com");
        System.out.println("password: 12345678");
        System.out.println("accessToken: \n" + response.getAccessToken());
        System.out.println("refreshToken: \n" + response.getRefreshToken());
        System.out.println("Documentation: http://localhost:8083/swagger-ui/index.html#/");
    }



    /**
     * Регистрирует нового пользователя. Email должен быть уникальным
     * @param request Данные пользователя, указанные при регистрации
     * @return UserJwtAuthenticationResponse - пара из access и refresh токенов
     * @see UserSignUpRequest
     * @see UserJwtAuthenticationResponse
     * @throws ValidationException При несоответствии правилам полей
     */
    public UserJwtAuthenticationResponse register(UserSignUpRequest request) {
        UserEntity user = userService.create(UserEntity.builder()
                .email(request.getEmail().strip())
                .password(passwordEncoder.encode(request.getPassword()))
                .build());

        return generateTokenResponse(user);
    }

    /**
     * Аутентифицирует существующего пользователя в системе и возвращает пару из access и refresh токенов
     * @param request Данные пользователя, указанные при входе
     * @return UserJwtAuthenticationResponse - пара из новых access и refresh токенов
     * @see UserSignInRequest
     * @see UserJwtAuthenticationResponse
     */
    public UserJwtAuthenticationResponse authenticate(UserSignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        UserEntity user = userService.getByEmail(request.getEmail());
        return generateTokenResponse(user);
    }

    /**
     * Выпускает новую пару access и refresh токенов по входящему refresh токену
     * @param request Refresh токен, передаваемый в теле запроса
     * @return UserJwtAuthenticationResponse - пара из новых access и refresh токенов
     * @see UserRefreshTokenRequest
     * @see UserJwtAuthenticationResponse
     */
    public UserJwtAuthenticationResponse refreshToken(UserRefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        TokenRedisEntity token = tokenRepository.getByToken(oldRefreshToken)
                .orElseThrow(() -> new AccessDeniedException("Token is invalid or expired"));

        if (!token.getTokenType().equals("REFRESH")) {
            throw new AccessDeniedException("Invalid token type. Expected REFRESH. Found " + token.getTokenType());
        }

        String email = jwtService.extractEmail(oldRefreshToken);
        if (email == null) {
            throw new AccessDeniedException("Provided refresh token is invalid");
        }

        UserEntity user = userService.getByEmail(email);
        tokenRepository.deleteAllByUserId(user.getUserId().toString());
        return generateTokenResponse(user);
    }

    private UserJwtAuthenticationResponse generateTokenResponse(UserEntity user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenRepository.save(new TokenRedisEntity(accessToken, accessExpTime, user.getUserId().toString() ,  "ACCESS"));
        tokenRepository.save(new TokenRedisEntity(refreshToken,refreshExpTime, user.getUserId().toString(), "REFRESH"));

        return UserJwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Boolean checkToken(String token) {

        System.out.println("Checking token from CheckToken!!!: " + token);
        try {
            String tokenType = jwtService.extractTokenType(token);

            if (StringUtils.isEmpty(tokenType) || tokenType.equals("REFRESH")) {
                return false;
            }
            System.out.println("result: " + tokenRepository.getByToken(token).isPresent());
            return tokenRepository.getByToken(token).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    public String getRoleByToken(String token) {
        return jwtService.extractRole(token);
    }

    public String extractEmail(String token) {
        return jwtService.extractEmail(token);
    }

}
