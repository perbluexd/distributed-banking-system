package com.banking.auth.application.service;

import com.banking.auth.application.command.RefreshCommand;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.JwtTokenPort;
import com.banking.auth.application.port.out.JwtTokenVerifierPort;
import com.banking.auth.application.port.out.RefreshTokenHasherPort;
import com.banking.auth.application.port.out.RefreshTokenRepositoryPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.security.jwt.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshServiceTest {

    @Mock
    private JwtTokenVerifierPort jwtTokenVerifier;

    @Mock
    private JwtTokenPort jwtTokenPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private ClockPort clockPort;

    @Mock
    private RefreshTokenHasherPort refreshTokenHasher;

    private JwtProperties jwtProperties;
    private RefreshService refreshService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessTtlSeconds(900);
        jwtProperties.setRefreshTtlSeconds(2592000);

        refreshService = new RefreshService(
                jwtTokenVerifier,
                jwtTokenPort,
                refreshTokenRepository,
                userRepository,
                clockPort,
                refreshTokenHasher,
                jwtProperties
        );
    }

    @Test
    void shouldRefreshSuccessfully() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token-hash",
                userId,
                now.plusSeconds(3600),
                false,
                false,
                now.minusSeconds(60)
        );

        User user = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                now.minusSeconds(1000)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");
        when(clockPort.now()).thenReturn(now);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenPort.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtTokenPort.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(refreshTokenHasher.hash("new-refresh-token")).thenReturn("new-refresh-token-hash");

        TokenPair result = refreshService.refresh(command);

        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals(900, result.getExpiresIn());
    }

    @Test
    void shouldThrowUnauthorizedWhenRefreshTokenIsBlank() {
        RefreshCommand command = new RefreshCommand("   ");

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verifyNoInteractions(jwtTokenVerifier, refreshTokenRepository, userRepository, jwtTokenPort);
    }

    @Test
    void shouldThrowUnauthorizedWhenStoredTokenDoesNotExist() {
        UUID userId = UUID.randomUUID();
        RefreshCommand command = new RefreshCommand("refresh-token");

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(userRepository, never()).findById(any());
        verify(jwtTokenPort, never()).generateAccessToken(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenHashDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "stored-hash",
                userId,
                now.plusSeconds(3600),
                false,
                false,
                now.minusSeconds(60)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("different-hash");

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(userRepository, never()).findById(any());
        verify(jwtTokenPort, never()).generateAccessToken(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenStoredTokenIsRevoked() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token-hash",
                userId,
                now.plusSeconds(3600),
                true,
                false,
                now.minusSeconds(60)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenStoredTokenIsUsed() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token-hash",
                userId,
                now.plusSeconds(3600),
                false,
                true,
                now.minusSeconds(60)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenStoredTokenIsExpired() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token-hash",
                userId,
                now.minusSeconds(1),
                false,
                false,
                now.minusSeconds(60)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");
        when(clockPort.now()).thenReturn(now);

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldThrowUnauthorizedWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshCommand command = new RefreshCommand("refresh-token");

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                "refresh-token-hash",
                userId,
                now.plusSeconds(3600),
                false,
                false,
                now.minusSeconds(60)
        );

        when(jwtTokenVerifier.verifyRefreshTokenAndGetUserId("refresh-token")).thenReturn(userId);
        when(refreshTokenRepository.findValidByUserId(userId)).thenReturn(Optional.of(stored));
        when(refreshTokenHasher.hash("refresh-token")).thenReturn("refresh-token-hash");
        when(clockPort.now()).thenReturn(now);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> refreshService.refresh(command));

        verify(jwtTokenPort, never()).generateAccessToken(any());
    }
}