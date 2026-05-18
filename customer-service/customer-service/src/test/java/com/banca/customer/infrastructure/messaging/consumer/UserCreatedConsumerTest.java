package com.banca.customer.infrastructure.messaging.consumer;

import com.banca.customer.application.command.CreateCustomerCommand;
import com.banca.customer.application.port.in.CreateCustomerUseCase;
import com.banca.customer.infrastructure.messaging.event.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserRegisteredConsumerTest {

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;

    private UserRegisteredConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UserRegisteredConsumer(createCustomerUseCase);
    }

    @Test
    void shouldConsumeUserRegisteredEventAndCreateCustomer() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        UserRegisteredEvent event = new UserRegisteredEvent(
                eventId,
                userId,
                "test@mail.com",
                occurredAt
        );

        consumer.consume(event);

        ArgumentCaptor<CreateCustomerCommand> captor =
                ArgumentCaptor.forClass(CreateCustomerCommand.class);

        verify(createCustomerUseCase).create(captor.capture());

        CreateCustomerCommand command = captor.getValue();

        assertEquals(userId, command.userId());
        assertEquals("test@mail.com", command.email());
    }

    @Test
    void shouldRethrowExceptionWhenCreateCustomerFails() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        UserRegisteredEvent event = new UserRegisteredEvent(
                eventId,
                userId,
                "test@mail.com",
                occurredAt
        );

        RuntimeException expectedException =
                new RuntimeException("Unexpected error");

        doThrow(expectedException)
                .when(createCustomerUseCase)
                .create(org.mockito.ArgumentMatchers.any(CreateCustomerCommand.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> consumer.consume(event)
        );

        assertEquals(expectedException, thrown);

        verify(createCustomerUseCase)
                .create(org.mockito.ArgumentMatchers.any(CreateCustomerCommand.class));
    }
}