package com.banking.auth.application.service;

import com.banking.auth.application.command.RegisterCommand;
import com.banking.auth.application.error.ConflictException;
import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.error.ValidationException;
import com.banking.auth.application.port.in.RegisterUseCase;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.OutboxEventRepositoryPort;
import com.banking.auth.application.port.out.PasswordHasherPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.messaging.event.UserRegisteredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class RegisterService implements RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    private static final String AGGREGATE_TYPE = "User";
    private static final String EVENT_TYPE = "UserRegisteredEvent";
    private static final String TOPIC = "user.registered";

    private final UserRepositoryPort userRepository;
    private final PasswordHasherPort passwordHasher;
    private final ClockPort clockPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public RegisterService(
            UserRepositoryPort userRepository,
            PasswordHasherPort passwordHasher,
            ClockPort clockPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.clockPort = clockPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public User register(RegisterCommand command) {

        if (command.getEmail() == null || command.getEmail().isBlank()) {
            throw new ValidationException(ErrorCode.INVALID_EMAIL, "Email is required");
        }

        if (command.getPassword() == null || command.getPassword().isBlank()) {
            throw new ValidationException(ErrorCode.WEAK_PASSWORD, "Password is required");
        }

        String normalizedEmail = command.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }

        String hashedPassword = passwordHasher.hash(command.getPassword());

        User user = new User(
                UUID.randomUUID(),
                normalizedEmail,
                hashedPassword,
                Set.of(Role.USER),
                clockPort.now()
        );

        User savedUser = userRepository.save(user);

        log.info(
                "[AUTH] User saved successfully. userId={}, email={}",
                savedUser.getId(),
                savedUser.getEmail()
        );

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                savedUser.getId(),
                savedUser.getEmail(),
                clockPort.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                AGGREGATE_TYPE,
                savedUser.getId(),
                EVENT_TYPE,
                TOPIC,
                savedUser.getId().toString(),
                toJson(event)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        log.info(
                "[AUTH] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}",
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                savedUser.getId(),
                savedUser.getId()
        );

        return savedUser;
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing event to JSON", e);
        }
    }
}