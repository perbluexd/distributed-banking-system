package com.banking.auth.infrastructure.persistence.adapter;

import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.banking.auth.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.banking.auth.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenPersistenceAdapterTest {

    @Mock
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Mock
    private RefreshTokenPersistenceMapper refreshTokenPersistenceMapper;

    @Mock
    private ClockPort clockPort;

    private RefreshTokenPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenPersistenceAdapter(
                refreshTokenJpaRepository,
                refreshTokenPersistenceMapper,
                clockPort
        );
    }

    @Test
    void shouldSaveRefreshToken() {
        UUID tokenId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RefreshToken domainToken = new RefreshToken(
                tokenId,
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T10:00:00Z")
        );

        RefreshTokenEntity entityToSave = mock(RefreshTokenEntity.class);
        RefreshTokenEntity savedEntity = mock(RefreshTokenEntity.class);
        RefreshToken mappedBack = new RefreshToken(
                tokenId,
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T10:00:00Z")
        );

        when(refreshTokenPersistenceMapper.toEntity(domainToken)).thenReturn(entityToSave);
        when(refreshTokenJpaRepository.save(entityToSave)).thenReturn(savedEntity);
        when(refreshTokenPersistenceMapper.toDomain(savedEntity)).thenReturn(mappedBack);

        RefreshToken result = adapter.save(domainToken);

        assertNotNull(result);
        assertEquals(tokenId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("token-hash", result.getTokenHash());

        verify(refreshTokenPersistenceMapper).toEntity(domainToken);
        verify(refreshTokenJpaRepository).save(entityToSave);
        verify(refreshTokenPersistenceMapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindValidRefreshTokenByUserId() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        RefreshTokenEntity entity = mock(RefreshTokenEntity.class);
        RefreshToken domainToken = new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T09:00:00Z")
        );

        when(clockPort.now()).thenReturn(now);
        when(refreshTokenJpaRepository
                .findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(userId, now))
                .thenReturn(Optional.of(entity));
        when(refreshTokenPersistenceMapper.toDomain(entity)).thenReturn(domainToken);

        Optional<RefreshToken> result = adapter.findValidByUserId(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());

        verify(clockPort).now();
        verify(refreshTokenJpaRepository)
                .findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(userId, now);
        verify(refreshTokenPersistenceMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenValidRefreshTokenDoesNotExist() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        when(clockPort.now()).thenReturn(now);
        when(refreshTokenJpaRepository
                .findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(userId, now))
                .thenReturn(Optional.empty());

        Optional<RefreshToken> result = adapter.findValidByUserId(userId);

        assertTrue(result.isEmpty());

        verify(clockPort).now();
        verify(refreshTokenJpaRepository)
                .findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(userId, now);
        verify(refreshTokenPersistenceMapper, never()).toDomain(any());
    }

    @Test
    void shouldRevokeAllRefreshTokensByUserId() {
        UUID userId = UUID.randomUUID();

        when(refreshTokenJpaRepository.revokeAllByUserId(userId)).thenReturn(3);

        adapter.revokeAllByUserId(userId);

        verify(refreshTokenJpaRepository).revokeAllByUserId(userId);
    }
}