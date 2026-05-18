package com.banca.customer.application.service;

import com.banca.customer.application.command.CreateCustomerCommand;
import com.banca.customer.application.error.ConflictException;
import com.banca.customer.application.port.out.CustomerRepositoryPort;
import com.banca.customer.application.port.out.OutboxEventRepositoryPort;
import com.banca.customer.domain.model.Customer;
import com.banca.customer.domain.model.OutboxEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @Mock
    private OutboxEventRepositoryPort outboxEventRepositoryPort;

    private ObjectMapper objectMapper;
    private CreateCustomerService createCustomerService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();

        createCustomerService = new CreateCustomerService(
                customerRepositoryPort,
                outboxEventRepositoryPort,
                objectMapper
        );
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        UUID userId = UUID.randomUUID();
        String email = "test@mail.com";

        CreateCustomerCommand command = new CreateCustomerCommand(userId, email);

        when(customerRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());
        when(customerRepositoryPort.existsByEmail(email)).thenReturn(false);
        when(customerRepositoryPort.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = createCustomerService.create(command);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals(email, result.getEmail());
        assertNotNull(result.getStatus());
        assertNotNull(result.getCreatedAt());

        ArgumentCaptor<Customer> customerCaptor =
                ArgumentCaptor.forClass(Customer.class);

        verify(customerRepositoryPort).save(customerCaptor.capture());

        Customer savedCustomer = customerCaptor.getValue();

        assertNotNull(savedCustomer.getId());
        assertEquals(userId, savedCustomer.getUserId());
        assertEquals(email, savedCustomer.getEmail());

        ArgumentCaptor<OutboxEvent> outboxCaptor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepositoryPort).save(outboxCaptor.capture());

        OutboxEvent savedOutboxEvent = outboxCaptor.getValue();

        assertNotNull(savedOutboxEvent);
        assertEquals("Customer", savedOutboxEvent.getAggregateType());
        assertEquals(result.getId(), savedOutboxEvent.getAggregateId());
        assertEquals("CustomerCreatedEvent", savedOutboxEvent.getEventType());
        assertEquals("customer.created", savedOutboxEvent.getTopic());
        assertEquals(result.getId().toString(), savedOutboxEvent.getEventKey());
        assertNotNull(savedOutboxEvent.getPayload());
        assertTrue(savedOutboxEvent.getPayload().contains(result.getId().toString()));
        assertTrue(savedOutboxEvent.getPayload().contains(userId.toString()));
        assertTrue(savedOutboxEvent.getPayload().contains(email));

        verify(customerRepositoryPort).findByUserId(userId);
        verify(customerRepositoryPort).existsByEmail(email);
    }

    @Test
    void shouldBeIdempotentWhenCustomerAlreadyExistsByUserId() {
        UUID userId = UUID.randomUUID();
        String email = "test@mail.com";

        Customer existingCustomer = Customer.create(userId, email);

        when(customerRepositoryPort.findByUserId(userId))
                .thenReturn(Optional.of(existingCustomer));

        Customer result = createCustomerService.create(
                new CreateCustomerCommand(userId, email)
        );

        assertNotNull(result);
        assertEquals(existingCustomer.getId(), result.getId());
        assertEquals(existingCustomer.getUserId(), result.getUserId());
        assertEquals(existingCustomer.getEmail(), result.getEmail());

        verify(customerRepositoryPort).findByUserId(userId);
        verify(customerRepositoryPort, never()).existsByEmail(anyString());
        verify(customerRepositoryPort, never()).save(any(Customer.class));
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExistsForAnotherUser() {
        UUID userId = UUID.randomUUID();
        String email = "test@mail.com";

        CreateCustomerCommand command = new CreateCustomerCommand(userId, email);

        when(customerRepositoryPort.findByUserId(userId)).thenReturn(Optional.empty());
        when(customerRepositoryPort.existsByEmail(email)).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> createCustomerService.create(command)
        );

        verify(customerRepositoryPort).findByUserId(userId);
        verify(customerRepositoryPort).existsByEmail(email);
        verify(customerRepositoryPort, never()).save(any(Customer.class));
        verifyNoInteractions(outboxEventRepositoryPort);
    }
}