package com.banking.auth.infrastructure.persistence.repository;

import com.banking.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findFirstByUserIdAndRevokedFalseAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            UUID userId, Instant now
    );

    @Modifying
    @Query("""
           update RefreshTokenEntity t
           set t.revoked = true
           where t.userId = :userId and t.revoked = false
           """)
    int revokeAllByUserId(UUID userId);

    @Modifying
    @Query("""
           update RefreshTokenEntity t
           set t.used = true
           where t.id = :tokenId and t.used = false
           """)
    int markUsed(UUID tokenId);
}
