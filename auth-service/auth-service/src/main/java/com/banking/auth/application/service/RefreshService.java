package com.banking.auth.application.service;

import com.banking.auth.application.command.RefreshCommand;
import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.in.RefreshUseCase;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.JwtTokenPort;
import com.banking.auth.application.port.out.JwtTokenVerifierPort;
import com.banking.auth.application.port.out.RefreshTokenHasherPort;
import com.banking.auth.application.port.out.RefreshTokenRepositoryPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.security.jwt.JwtProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshService implements RefreshUseCase {

    private final JwtTokenVerifierPort jwtTokenVerifier;
    private final JwtTokenPort jwtTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final ClockPort clockPort;
    private final RefreshTokenHasherPort refreshTokenHasher;
    private final JwtProperties jwtProperties;

    public RefreshService(JwtTokenVerifierPort jwtTokenVerifier,
                          JwtTokenPort jwtTokenPort,
                          RefreshTokenRepositoryPort refreshTokenRepository,
                          UserRepositoryPort userRepository,
                          ClockPort clockPort,
                          RefreshTokenHasherPort refreshTokenHasher,
                          JwtProperties jwtProperties) {
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.jwtTokenPort = jwtTokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.clockPort = clockPort;
        this.refreshTokenHasher = refreshTokenHasher;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public TokenPair refresh(RefreshCommand command) {

        if (command.getRefreshToken() == null || command.getRefreshToken().isBlank()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        UUID userId = jwtTokenVerifier.verifyRefreshTokenAndGetUserId(command.getRefreshToken());

        RefreshToken stored = refreshTokenRepository.findValidByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        String incomingHash = refreshTokenHasher.hash(command.getRefreshToken());
        if (!incomingHash.equals(stored.getTokenHash())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        if (stored.isRevoked() || stored.isUsed() || stored.getExpiresAt().isBefore(clockPort.now())) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token"));

        // 1) invalidar el refresh token consumido
        refreshTokenRepository.markUsed(stored.getId());

        // 2) generar nuevos tokens
        String newAccessToken = jwtTokenPort.generateAccessToken(user);
        String newRefreshToken = jwtTokenPort.generateRefreshToken(user);

        // 3) guardar el nuevo refresh token hasheado
        RefreshToken newStoredToken = new RefreshToken(
                UUID.randomUUID(),
                refreshTokenHasher.hash(newRefreshToken),
                user.getId(),
                clockPort.now().plusSeconds(jwtProperties.getRefreshTtlSeconds()),
                false,
                false,
                clockPort.now()
        );

        refreshTokenRepository.save(newStoredToken);

        // 4) devolver ambos nuevos
        return new TokenPair(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtProperties.getAccessTtlSeconds()
        );
    }
}