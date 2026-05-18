package com.banking.auth.infrastructure.persistence.adapter;

import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.RefreshTokenRepositoryPort;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import com.banking.auth.infrastructure.persistence.mapper.RefreshTokenPersistenceMapper;
import com.banking.auth.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenPersistenceMapper refreshTokenPersistenceMapper;
    private final ClockPort clockPort;

    public RefreshTokenPersistenceAdapter(
            RefreshTokenJpaRepository refreshTokenJpaRepository,
            RefreshTokenPersistenceMapper refreshTokenPersistenceMapper,
            ClockPort clockPort
    ) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.refreshTokenPersistenceMapper = refreshTokenPersistenceMapper;
        this.clockPort = clockPort;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = refreshTokenPersistenceMapper.toEntity(refreshToken);
        RefreshTokenEntity savedEntity = refreshTokenJpaRepository.save(entity);
        return refreshTokenPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findValidByUserId(UUID userId) {
        return refreshTokenJpaRepository
                .findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId,
                        clockPort.now()
                )
                .map(refreshTokenPersistenceMapper::toDomain);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        refreshTokenJpaRepository.revokeAllByUserId(userId);
    }

    @Override
    public void markUsed(UUID tokenId) {
        refreshTokenJpaRepository.markUsed(tokenId);
    }
}