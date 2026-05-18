package com.banking.gateway.security;

import com.banking.gateway.config.JwtConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "cambia-esto-por-una-clave-larga-de-minimo-32-caracteres-123456";
    private static final String ISSUER = "auth-service";

    private JwtAuthenticationFilter filter;
    private SecretKey key;
    private TestJwtAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        JwtConfig config = new JwtConfig();
        config.setSecret(SECRET);
        config.setIssuer(ISSUER);

        JwtService jwtService = new JwtService(config);

        entryPoint = new TestJwtAuthenticationEntryPoint();

        filter = new JwtAuthenticationFilter(jwtService, entryPoint);
        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldContinueWithoutToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/me").build()
        );

        AtomicReference<Boolean> chainCalled = new AtomicReference<>(false);

        WebFilterChain chain = ex -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(chainCalled.get()).isTrue();
        assertThat(entryPoint.wasCalled()).isFalse();
    }

    @Test
    void shouldAuthenticateWithValidToken() {
        String token = validToken(UUID.randomUUID());

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build()
        );

        AtomicReference<String> principal = new AtomicReference<>();

        WebFilterChain chain = ex -> ReactiveSecurityContextHolder.getContext()
                .doOnNext(ctx -> principal.set(ctx.getAuthentication().getName()))
                .then();

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(principal.get()).isNotBlank();
        assertThat(entryPoint.wasCalled()).isFalse();
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                        .build()
        );

        AtomicReference<Boolean> chainCalled = new AtomicReference<>(false);

        WebFilterChain chain = ex -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(chainCalled.get()).isFalse();
        assertThat(entryPoint.wasCalled()).isTrue();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String validToken(UUID userId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .claim("type", "access")
                .claim("email", "test@example.com")
                .claim("roles", List.of("USER"))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private static class TestJwtAuthenticationEntryPoint extends JwtAuthenticationEntryPoint {

        private boolean called = false;

        TestJwtAuthenticationEntryPoint() {
            super(new ObjectMapper());
        }

        @Override
        public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
            called = true;
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        boolean wasCalled() {
            return called;
        }
    }
}