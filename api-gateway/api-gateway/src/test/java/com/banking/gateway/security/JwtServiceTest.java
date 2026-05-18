package com.banking.gateway.security;

import com.banking.gateway.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "cambia-esto-por-una-clave-larga-de-minimo-32-caracteres-123456";
    private static final String ISSUER = "auth-service";

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        JwtConfig config = new JwtConfig();
        config.setSecret(SECRET);
        config.setIssuer(ISSUER);

        jwtService = new JwtService(config);
        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldValidateValidAccessToken() {
        UUID userId = UUID.randomUUID();

        String token = token(userId, ISSUER, "access", Instant.now().plusSeconds(900), key);

        JwtUser result = jwtService.validateAccessToken(token);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.roles()).contains("USER");
    }

    @Test
    void shouldRejectExpiredToken() {
        String token = token(UUID.randomUUID(), ISSUER, "access", Instant.now().minusSeconds(60), key);

        assertThatThrownBy(() -> jwtService.validateAccessToken(token))
                .isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "otra-clave-larga-de-minimo-32-caracteres-para-test-123".getBytes(StandardCharsets.UTF_8)
        );

        String token = token(UUID.randomUUID(), ISSUER, "access", Instant.now().plusSeconds(900), wrongKey);

        assertThatThrownBy(() -> jwtService.validateAccessToken(token))
                .isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void shouldRejectTokenWithInvalidIssuer() {
        String token = token(UUID.randomUUID(), "wrong-issuer", "access", Instant.now().plusSeconds(900), key);

        assertThatThrownBy(() -> jwtService.validateAccessToken(token))
                .isInstanceOf(InvalidJwtException.class);
    }

    private String token(UUID userId, String issuer, String type, Instant expiration, SecretKey signingKey) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("type", type)
                .claim("email", "test@example.com")
                .claim("roles", List.of("USER"))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }
}