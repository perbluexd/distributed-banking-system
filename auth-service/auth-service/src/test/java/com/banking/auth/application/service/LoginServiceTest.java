package com.banking.auth.application.service;

import com.banking.auth.application.command.LoginCommand;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.JwtTokenPort;
import com.banking.auth.application.port.out.OutboxEventRepositoryPort;
import com.banking.auth.application.port.out.PasswordHasherPort;
import com.banking.auth.application.port.out.RefreshTokenHasherPort;
import com.banking.auth.application.port.out.RefreshTokenRepositoryPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.security.jwt.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordHasherPort passwordHasher;

    @Mock
    private JwtTokenPort jwtTokenPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private RefreshTokenHasherPort refreshTokenHasher;

    @Mock
    private ClockPort clockPort;

    @Mock
    private OutboxEventRepositoryPort outboxEventRepositoryPort;

    private JwtProperties jwtProperties;
    private ObjectMapper objectMapper;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTtlSeconds(900);
        jwtProperties.setRefreshTtlSeconds(2592000);

        objectMapper = new ObjectMapper().findAndRegisterModules();

        loginService = new LoginService(
                userRepository,
                passwordHasher,
                jwtTokenPort,
                refreshTokenRepository,
                refreshTokenHasher,
                clockPort,
                jwtProperties,
                outboxEventRepositoryPort,
                objectMapper
        );
    }

    @Test
    void shouldLoginSuccessfully() {
        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        LoginCommand command = new LoginCommand("USER@MAIL.COM", "12345678");
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("12345678", "hashed-password")).thenReturn(true);
        when(jwtTokenPort.generateAccessToken(user)).thenReturn("access-token");
        when(jwtTokenPort.generateRefreshToken(user)).thenReturn("refresh-token");
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");
        when(clockPort.now()).thenReturn(now);

        TokenPair result = loginService.login(command);

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(900, result.getExpiresIn());

        verify(userRepository).findByEmail("user@mail.com");
        verify(passwordHasher).matches("12345678", "hashed-password");
        verify(refreshTokenRepository).revokeAllByUserId(userId);

        ArgumentCaptor<RefreshToken> refreshTokenCaptor =
                ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        RefreshToken savedToken = refreshTokenCaptor.getValue();

        assertNotNull(savedToken.getId());
        assertEquals(userId, savedToken.getUserId());
        assertEquals("refresh-token-hash", savedToken.getTokenHash());
        assertEquals(now.plusSeconds(2592000), savedToken.getExpiresAt());
        assertFalse(savedToken.isRevoked());
        assertFalse(savedToken.isUsed());
        assertEquals(now, savedToken.getCreatedAt());

        ArgumentCaptor<OutboxEvent> outboxCaptor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepositoryPort).save(outboxCaptor.capture());

        OutboxEvent savedOutboxEvent = outboxCaptor.getValue();

        assertNotNull(savedOutboxEvent);
        assertEquals("USER", savedOutboxEvent.getAggregateType());
        assertEquals(userId, savedOutboxEvent.getAggregateId());
        assertEquals("UserLoggedInEvent", savedOutboxEvent.getEventType());
        assertEquals("user-logged-in", savedOutboxEvent.getTopic());
        assertEquals(userId.toString(), savedOutboxEvent.getEventKey());
        assertNotNull(savedOutboxEvent.getPayload());
        assertTrue(savedOutboxEvent.getPayload().contains(userId.toString()));
        assertTrue(savedOutboxEvent.getPayload().contains("user@mail.com"));
    }

    @Test
    void shouldThrowUnauthorizedWhenEmailIsNull() {
        LoginCommand command = new LoginCommand(null, "12345678");

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                jwtTokenPort,
                refreshTokenRepository,
                refreshTokenHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowUnauthorizedWhenEmailIsBlank() {
        LoginCommand command = new LoginCommand("   ", "12345678");

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                jwtTokenPort,
                refreshTokenRepository,
                refreshTokenHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordIsNull() {
        LoginCommand command = new LoginCommand("user@mail.com", null);

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                jwtTokenPort,
                refreshTokenRepository,
                refreshTokenHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordIsBlank() {
        LoginCommand command = new LoginCommand("user@mail.com", "   ");

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                jwtTokenPort,
                refreshTokenRepository,
                refreshTokenHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowUnauthorizedWhenUserDoesNotExist() {
        LoginCommand command = new LoginCommand("USER@MAIL.COM", "12345678");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verify(userRepository).findByEmail("user@mail.com");
        verify(passwordHasher, never()).matches(anyString(), anyString());
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
        verify(refreshTokenRepository, never()).save(any());
        verify(outboxEventRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordDoesNotMatch() {
        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-01T10:00:00Z")
        );

        LoginCommand command = new LoginCommand("user@mail.com", "wrong-password");

        when(userRepository.findByEmail("user@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordHasher.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> loginService.login(command));

        verify(userRepository).findByEmail("user@mail.com");
        verify(passwordHasher).matches("wrong-password", "hashed-password");
        verify(refreshTokenRepository, never()).revokeAllByUserId(any());
        verify(refreshTokenRepository, never()).save(any());
        verify(jwtTokenPort, never()).generateAccessToken(any());
        verify(jwtTokenPort, never()).generateRefreshToken(any());
        verify(outboxEventRepositoryPort, never()).save(any());
    }
}