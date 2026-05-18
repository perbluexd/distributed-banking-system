package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.infrastructure.messaging.event.AccountCreatedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AccountCreatedConsumerTest {

    private final AccountEventHandlerService accountEventHandlerService =
            mock(AccountEventHandlerService.class);

    private final AccountCreatedConsumer consumer =
            new AccountCreatedConsumer(accountEventHandlerService);

    @Test
    void shouldHandleAccountCreatedEvent() {
        AccountCreatedEvent event = new AccountCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACC-123",
                "SAVINGS",
                new BigDecimal("100.00"),
                Currency.PEN,
                Instant.now()
        );

        consumer.consume(event);

        verify(accountEventHandlerService).handleAccountCreated(
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );
    }
}