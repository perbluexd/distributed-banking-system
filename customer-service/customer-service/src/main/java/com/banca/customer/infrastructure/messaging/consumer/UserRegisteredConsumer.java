package com.banca.customer.infrastructure.messaging.consumer;

import com.banca.customer.application.command.CreateCustomerCommand;
import com.banca.customer.application.port.in.CreateCustomerUseCase;
import com.banca.customer.infrastructure.messaging.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserRegisteredConsumer.class);

    private final CreateCustomerUseCase createCustomerUseCase;

    public UserRegisteredConsumer(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @KafkaListener(
            topics = "user.registered",
            groupId = "customer-service"
    )
    public void consume(UserRegisteredEvent event) {

        log.info(
                "[CUSTOMER] UserRegisteredEvent received. userId={}, email={}",
                event.userId(),
                event.email()
        );

        try {
            CreateCustomerCommand command = new CreateCustomerCommand(
                    event.userId(),
                    event.email()
            );

            createCustomerUseCase.create(command);

            log.info(
                    "[CUSTOMER] UserRegisteredEvent processed successfully. userId={}, email={}",
                    event.userId(),
                    event.email()
            );

        } catch (Exception ex) {
            log.error(
                    "[CUSTOMER] Error processing UserRegisteredEvent. userId={}, email={}",
                    event.userId(),
                    event.email(),
                    ex
            );

            throw ex;
        }
    }
}