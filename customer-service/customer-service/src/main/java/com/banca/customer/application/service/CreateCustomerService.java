package com.banca.customer.application.service;

import com.banca.customer.application.command.CreateCustomerCommand;
import com.banca.customer.application.error.ConflictException;
import com.banca.customer.application.error.ErrorCode;
import com.banca.customer.application.port.in.CreateCustomerUseCase;
import com.banca.customer.application.port.out.CustomerRepositoryPort;
import com.banca.customer.application.port.out.OutboxEventRepositoryPort;
import com.banca.customer.domain.model.Customer;
import com.banca.customer.domain.model.OutboxEvent;
import com.banca.customer.infrastructure.messaging.event.CustomerCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateCustomerService implements CreateCustomerUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCustomerService.class);

    private static final String AGGREGATE_TYPE = "Customer";
    private static final String EVENT_TYPE = "CustomerCreatedEvent";
    private static final String TOPIC = "customer.created";

    private final CustomerRepositoryPort customerRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public CreateCustomerService(
            CustomerRepositoryPort customerRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.customerRepositoryPort = customerRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Customer create(CreateCustomerCommand command) {

        return customerRepositoryPort.findByUserId(command.userId())
                .orElseGet(() -> {
                    if (customerRepositoryPort.existsByEmail(command.email())) {
                        throw new ConflictException(
                                ErrorCode.EMAIL_ALREADY_EXISTS,
                                "Customer already exists with this email"
                        );
                    }

                    Customer customer = Customer.create(
                            command.userId(),
                            command.email()
                    );

                    Customer savedCustomer = customerRepositoryPort.save(customer);

                    log.info(
                            "[CUSTOMER] Customer created successfully. customerId={}, userId={}, email={}, status={}",
                            savedCustomer.getId(),
                            savedCustomer.getUserId(),
                            savedCustomer.getEmail(),
                            savedCustomer.getStatus()
                    );

                    CustomerCreatedEvent event = new CustomerCreatedEvent(
                            UUID.randomUUID(),
                            savedCustomer.getId(),
                            savedCustomer.getUserId(),
                            savedCustomer.getEmail(),
                            savedCustomer.getStatus(),
                            savedCustomer.getCreatedAt()
                    );

                    OutboxEvent outboxEvent = OutboxEvent.create(
                            AGGREGATE_TYPE,
                            savedCustomer.getId(),
                            EVENT_TYPE,
                            TOPIC,
                            savedCustomer.getId().toString(),
                            toJson(event)
                    );

                    outboxEventRepositoryPort.save(outboxEvent);

                    log.info(
                            "[CUSTOMER] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}",
                            EVENT_TYPE,
                            TOPIC,
                            AGGREGATE_TYPE,
                            savedCustomer.getId(),
                            savedCustomer.getId()
                    );

                    return savedCustomer;
                });
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing event to JSON", e);
        }
    }
}