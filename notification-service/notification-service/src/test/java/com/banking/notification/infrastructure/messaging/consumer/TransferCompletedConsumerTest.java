package com.banking.notification.infrastructure.messaging.consumer;

import com.banking.notification.application.port.in.ProcessTransferCompletedNotificationUseCase;
import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TransferCompletedConsumerTest {

    private ProcessTransferCompletedNotificationUseCase useCase;
    private TransferCompletedConsumer consumer;

    @BeforeEach
    void setUp() {
        useCase = mock(ProcessTransferCompletedNotificationUseCase.class);
        consumer = new TransferCompletedConsumer(useCase);
    }

    @Test
    void shouldConsumeTransferCompletedEvent() {
        TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        consumer.consume(event);

        verify(useCase).process(event);
    }
}