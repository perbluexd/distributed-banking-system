package com.banking.auth.infrastructure.security.jwt;

import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenVerifierAdapterTest {

    private JwtService jwtService;
    private JwtTokenVerifier jwtTokenVerifier;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("change-me-change-me-change-me-123456");
        jwtProperties.setIssuer("auth-service");
        jwtProperties.setAccessTtlSeconds(900);
        jwtProperties.setRefreshTtlSeconds(2592000);

        jwtService = new JwtService(jwtProperties);
        jwtTokenVerifier = new JwtTokenVerifier(jwtProperties);
    }

    @Test
    void shouldVerifyAccessTokenAndReturnUserId() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String token = jwtService.generateAccessToken(user);

        UUID result = jwtTokenVerifier.verifyAccessTokenAndGetUserId(token);

        assertEquals(userId, result);
    }

    @Test
    void shouldVerifyRefreshTokenAndReturnUserId() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String token = jwtService.generateRefreshToken(user);

        UUID result = jwtTokenVerifier.verifyRefreshTokenAndGetUserId(token);

        assertEquals(userId, result);
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenAccessTokenIsInvalid() {
        String invalidToken = "this.is.not.valid";

        assertThrows(UnauthorizedException.class, () ->
                jwtTokenVerifier.verifyAccessTokenAndGetUserId(invalidToken)
        );
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenRefreshTokenIsInvalid() {
        String invalidToken = "this.is.not.valid";

        assertThrows(UnauthorizedException.class, () ->
                jwtTokenVerifier.verifyRefreshTokenAndGetUserId(invalidToken)
        );
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUsingAccessTokenAsRefreshToken() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String accessToken = jwtService.generateAccessToken(user);

        assertThrows(UnauthorizedException.class, () ->
                jwtTokenVerifier.verifyRefreshTokenAndGetUserId(accessToken)
        );
    }

    @Test
    void shouldThrowUnauthorizedExceptionWhenUsingRefreshTokenAsAccessToken() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String refreshToken = jwtService.generateRefreshToken(user);

        assertThrows(UnauthorizedException.class, () ->
                jwtTokenVerifier.verifyAccessTokenAndGetUserId(refreshToken)
        );
    }
}