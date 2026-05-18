package com.banking.auth.infrastructure.persistence.mapper;

import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenPersistenceMapper {

    default RefreshTokenEntity toEntity(RefreshToken token) {
        return new RefreshTokenEntity(
                token.getId(),
                token.getTokenHash(),
                token.getUserId(),
                token.getExpiresAt(),
                token.isRevoked(),
                token.isUsed(),
                token.getCreatedAt()
        );
    }

    default RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getTokenHash(),
                entity.getUserId(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.isUsed(),
                entity.getCreatedAt()
        );
    }
}
