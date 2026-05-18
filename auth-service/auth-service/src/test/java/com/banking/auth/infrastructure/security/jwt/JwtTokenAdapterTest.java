package com.banking.auth.infrastructure.security.jwt;


import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenAdapterTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("change-me-change-me-change-me-123456");
        jwtProperties.setIssuer("auth-service");
        jwtProperties.setAccessTtlSeconds(900);
        jwtProperties.setRefreshTtlSeconds(2592000);

        jwtService = new JwtService(jwtProperties);
    }

    @Test
    void shouldGenerateAccessToken() {
        User user = new User(
                UUID.randomUUID(),
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldGenerateRefreshToken() {
        User user = new User(
                UUID.randomUUID(),
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String token = jwtService.generateRefreshToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldGenerateDifferentTokensForAccessAndRefresh() {
        User user = new User(
                UUID.randomUUID(),
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
        assertNotEquals(accessToken, refreshToken);
    }
}