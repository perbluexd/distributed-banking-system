package com.banking.notification.infrastructure.messaging.consumer;

import com.banking.notification.application.port.in.ProcessTransferFailedNotificationUseCase;
import com.banking.notification.infrastructure.messaging.event.TransferFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferFailedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferFailedConsumer.class);

    private final ProcessTransferFailedNotificationUseCase processTransferFailedNotificationUseCase;

    public TransferFailedConsumer(
            ProcessTransferFailedNotificationUseCase processTransferFailedNotificationUseCase
    ) {
        this.processTransferFailedNotificationUseCase = processTransferFailedNotificationUseCase;
    }

    @KafkaListener(
            topics = "transfer.failed",
            groupId = "notification-service",
            containerFactory = "transferFailedKafkaListenerContainerFactory"
    )
    public void consume(TransferFailedEvent event) {

        log.info(
                "[NOTIFICATION] TransferFailedEvent received. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}, reason={}",
                event.transferId(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount(),
                event.currency(),
                event.status(),
                event.reason()
        );

        processTransferFailedNotificationUseCase.process(event);

        log.info(
                "[NOTIFICATION] TransferFailedEvent processed successfully. transferId={}",
                event.transferId()
        );
    }
}