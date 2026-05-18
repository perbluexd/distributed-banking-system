package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.infrastructure.messaging.event.AccountDebitedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AccountDebitedConsumerTest {

    private final AccountEventHandlerService accountEventHandlerService =
            mock(AccountEventHandlerService.class);

    private final AccountDebitedConsumer consumer =
            new AccountDebitedConsumer(accountEventHandlerService);

    @Test
    void shouldHandleAccountDebitedEvent() {
        AccountDebitedEvent event = new AccountDebitedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACC-123",
                new BigDecimal("100.00"),
                new BigDecimal("400.00"),
                Currency.PEN,
                Instant.now()
        );

        consumer.consume(event);

        verify(accountEventHandlerService).handleAccountDebited(
                event.transferId(),
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );
    }
}