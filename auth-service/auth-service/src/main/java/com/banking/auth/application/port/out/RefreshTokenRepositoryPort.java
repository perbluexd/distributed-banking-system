package com.banking.auth.application.port.out;

import com.banking.auth.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findValidByUserId(UUID userId);

    void revokeAllByUserId(UUID userId);

    void markUsed(UUID refreshTokenId);

}
