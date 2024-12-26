package org.example.authservice.security;

import com.auth0.jwt.interfaces.Claim;
import org.example.authservice.domain.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserEntity userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        setPrivateField(jwtService, "jwtSigningKey", "testSigningKey");
        setPrivateField(jwtService, "jwtExpiration", 3600L); // 1 hour
        setPrivateField(jwtService, "jwtRefreshExpiration", 7200L); // 2 hours
    }

    private void setPrivateField(Object targetObject, String fieldName, Object fieldValue) throws Exception {
        Field field = targetObject.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(targetObject, fieldValue);
    }

    private String callPrepareToken(JwtService jwtService, HashMap<String, Object> claims) throws Exception {
        Method method = JwtService.class.getDeclaredMethod("prepareToken", HashMap.class);
        method.setAccessible(true);
        return (String) method.invoke(jwtService, claims);
    }

    @Test
    void testGenerateAccessToken() {
        when(userDetails.getUserId()).thenReturn(1L);
        when(userDetails.getEmail()).thenReturn("test@example.com");
        when(userDetails.getRole()).thenReturn(UserEntity.Role.USER);

        String token = jwtService.generateAccessToken(userDetails);
        assertNotNull(token);

        Map<String, Claim> claims = jwtService.getTokenPayload(token);
        assertEquals("test@example.com", claims.get("email").asString());
        assertEquals("USER", claims.get("role").asString());
        assertEquals("ACCESS", claims.get("tokenType").asString());
    }

    @Test
    void testGenerateRefreshToken() {
        when(userDetails.getUserId()).thenReturn(1L);
        when(userDetails.getEmail()).thenReturn("test@example.com");
        when(userDetails.getRole()).thenReturn(UserEntity.Role.USER);

        String token = jwtService.generateRefreshToken(userDetails);
        assertNotNull(token);

        Map<String, Claim> claims = jwtService.getTokenPayload(token);
        assertEquals("test@example.com", claims.get("email").asString());
        assertEquals("USER", claims.get("role").asString());
        assertEquals("REFRESH", claims.get("tokenType").asString());
    }

    @Test
    void testGetTokenPayload() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");
        claims.put("role", "USER");
        claims.put("tokenType", "ACCESS");
        claims.put("expires", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

        try {
            String token = callPrepareToken(jwtService, claims);
            assertNotNull(token);

            Map<String, Claim> payload = jwtService.getTokenPayload(token);
            assertEquals("test@example.com", payload.get("email").asString());
            assertEquals("USER", payload.get("role").asString());
            assertEquals("ACCESS", payload.get("tokenType").asString());
        } catch (Exception e) {
            fail("Failed to call prepareToken: " + e.getMessage());
        }
    }

    @Test
    void testExtractEmail() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", "test@example.com");

        try {
            String token = callPrepareToken(jwtService, claims);
            assertNotNull(token);

            String email = jwtService.extractEmail(token);
            assertEquals("test@example.com", email);
        } catch (Exception e) {
            fail("Failed to call prepareToken: " + e.getMessage());
        }
    }

    @Test
    void testExtractTokenType() {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "ACCESS");

        try {
            String token = callPrepareToken(jwtService, claims);
            assertNotNull(token);

            String tokenType = jwtService.extractTokenType(token);
            assertEquals("ACCESS", tokenType);
        } catch (Exception e) {
            fail("Failed to call prepareToken: " + e.getMessage());
        }
    }
}
