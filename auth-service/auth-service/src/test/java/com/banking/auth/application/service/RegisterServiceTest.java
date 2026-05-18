package com.banking.auth.application.service;

import com.banking.auth.application.command.RegisterCommand;
import com.banking.auth.application.error.ConflictException;
import com.banking.auth.application.error.ValidationException;
import com.banking.auth.application.port.out.ClockPort;
import com.banking.auth.application.port.out.OutboxEventRepositoryPort;
import com.banking.auth.application.port.out.PasswordHasherPort;
import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordHasherPort passwordHasher;

    @Mock
    private ClockPort clockPort;

    @Mock
    private OutboxEventRepositoryPort outboxEventRepositoryPort;

    private ObjectMapper objectMapper;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        registerService = new RegisterService(
                userRepository,
                passwordHasher,
                clockPort,
                outboxEventRepositoryPort,
                objectMapper
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterCommand command = new RegisterCommand("TEST@MAIL.COM", "12345678");
        Instant now = Instant.parse("2026-03-13T10:00:00Z");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordHasher.hash("12345678")).thenReturn("hashed-password");
        when(clockPort.now()).thenReturn(now);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = registerService.register(command);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("test@mail.com", result.getEmail());
        assertEquals("hashed-password", result.getPasswordHash());
        assertEquals(Set.of(Role.USER), result.getRoles());
        assertEquals(now, result.getCreatedAt());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertNotNull(savedUser.getId());
        assertEquals("test@mail.com", savedUser.getEmail());
        assertEquals("hashed-password", savedUser.getPasswordHash());
        assertEquals(Set.of(Role.USER), savedUser.getRoles());
        assertEquals(now, savedUser.getCreatedAt());

        verify(userRepository).existsByEmail("test@mail.com");
        verify(passwordHasher).hash("12345678");

        ArgumentCaptor<OutboxEvent> outboxCaptor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepositoryPort).save(outboxCaptor.capture());

        OutboxEvent savedOutboxEvent = outboxCaptor.getValue();

        assertNotNull(savedOutboxEvent);
        assertEquals("User", savedOutboxEvent.getAggregateType());
        assertEquals(savedUser.getId(), savedOutboxEvent.getAggregateId());
        assertEquals("UserRegisteredEvent", savedOutboxEvent.getEventType());
        assertEquals("user.registered", savedOutboxEvent.getTopic());
        assertEquals(savedUser.getId().toString(), savedOutboxEvent.getEventKey());
        assertNotNull(savedOutboxEvent.getPayload());
        assertTrue(savedOutboxEvent.getPayload().contains(savedUser.getId().toString()));
        assertTrue(savedOutboxEvent.getPayload().contains("test@mail.com"));
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsNull() {
        RegisterCommand command = new RegisterCommand(null, "12345678");

        assertThrows(ValidationException.class, () -> registerService.register(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowValidationExceptionWhenEmailIsBlank() {
        RegisterCommand command = new RegisterCommand("   ", "12345678");

        assertThrows(ValidationException.class, () -> registerService.register(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowValidationExceptionWhenPasswordIsNull() {
        RegisterCommand command = new RegisterCommand("test@mail.com", null);

        assertThrows(ValidationException.class, () -> registerService.register(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowValidationExceptionWhenPasswordIsBlank() {
        RegisterCommand command = new RegisterCommand("test@mail.com", "   ");

        assertThrows(ValidationException.class, () -> registerService.register(command));

        verifyNoInteractions(
                userRepository,
                passwordHasher,
                clockPort,
                outboxEventRepositoryPort
        );
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
        RegisterCommand command = new RegisterCommand("TEST@MAIL.COM", "12345678");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> registerService.register(command));

        verify(userRepository).existsByEmail("test@mail.com");
        verifyNoInteractions(passwordHasher, clockPort, outboxEventRepositoryPort);
        verify(userRepository, never()).save(any(User.class));
    }
}