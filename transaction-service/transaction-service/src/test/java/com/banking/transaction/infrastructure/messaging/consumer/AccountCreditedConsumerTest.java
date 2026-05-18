package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.infrastructure.messaging.event.AccountCreditedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AccountCreditedConsumerTest {

    private final AccountEventHandlerService accountEventHandlerService =
            mock(AccountEventHandlerService.class);

    private final AccountCreditedConsumer consumer =
            new AccountCreditedConsumer(accountEventHandlerService);

    @Test
    void shouldHandleAccountCreditedEvent() {
        AccountCreditedEvent event = new AccountCreditedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACC-456",
                new BigDecimal("100.00"),
                new BigDecimal("300.00"),
                Currency.PEN,
                Instant.now()
        );

        consumer.consume(event);

        verify(accountEventHandlerService).handleAccountCredited(
                event.transferId(),
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );
    }
}