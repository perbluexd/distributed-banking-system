package com.banking.auth.application.service;

import com.banking.auth.application.command.LoginCommand;
import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.model.TokenPair;
import com.banking.auth.application.port.in.LoginUseCase;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.JwtTokenPort;
import com.banking.auth.application.port.out.OutboxEventRepositoryPort;
import com.banking.auth.application.port.out.PasswordHasherPort;
import com.banking.auth.application.port.out.RefreshTokenHasherPort;
import com.banking.auth.application.port.out.RefreshTokenRepositoryPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.domain.model.RefreshToken;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.messaging.event.UserLoggedInEvent;
import com.banking.auth.infrastructure.security.jwt.JwtProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginService implements LoginUseCase {

    private static final String USER_AGGREGATE_TYPE = "USER";
    private static final String USER_LOGGED_IN_EVENT_TYPE = "UserLoggedInEvent";
    private static final String USER_LOGGED_IN_TOPIC = "user-logged-in";

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final JwtTokenPort jwtTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final RefreshTokenHasherPort refreshTokenHasher;
    private final ClockPort clockPort;
    private final JwtProperties jwtProperties;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public LoginService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            JwtTokenPort jwtTokenPort,
            RefreshTokenRepositoryPort refreshTokenRepository,
            RefreshTokenHasherPort refreshTokenHasher,
            ClockPort clockPort,
            JwtProperties jwtProperties,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtTokenPort = jwtTokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenHasher = refreshTokenHasher;
        this.clockPort = clockPort;
        this.jwtProperties = jwtProperties;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    public TokenPair login(LoginCommand command) {

        if (command.getEmail() == null || command.getEmail().isBlank()
                || command.getPassword() == null || command.getPassword().isBlank()) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        String emailNormalized = command.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(emailNormalized)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid credentials"));

        boolean ok = passwordHasher.matches(command.getPassword(), user.getPasswordHash());
        if (!ok) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid credentials");
        }

        refreshTokenRepository.revokeAllByUserId(user.getId());

        String accessToken = jwtTokenPort.generateAccessToken(user);
        String refreshToken = jwtTokenPort.generateRefreshToken(user);

        Instant now = clockPort.now();

        RefreshToken stored = new RefreshToken(
                UUID.randomUUID(),
                refreshTokenHasher.hash(refreshToken),
                user.getId(),
                now.plusSeconds(jwtProperties.getRefreshTtlSeconds()),
                false,
                false,
                now
        );

        refreshTokenRepository.save(stored);

        createUserLoggedInOutboxEvent(user, now);

        return new TokenPair(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.getAccessTtlSeconds()
        );
    }

    private void createUserLoggedInOutboxEvent(User user, Instant occurredAt) {
        UserLoggedInEvent event = new UserLoggedInEvent(
                UUID.randomUUID(),
                user.getId(),
                user.getEmail(),
                occurredAt
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                USER_AGGREGATE_TYPE,
                user.getId(),
                USER_LOGGED_IN_EVENT_TYPE,
                USER_LOGGED_IN_TOPIC,
                user.getId().toString(),
                toJson(event)
        );

        outboxEventRepositoryPort.save(outboxEvent);
    }

    private String toJson(UserLoggedInEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing UserLoggedInEvent", e);
        }
    }
}