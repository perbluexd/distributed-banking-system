package com.banking.notification.infrastructure.messaging.consumer;

import com.banking.notification.application.port.in.ProcessTransferCompletedNotificationUseCase;
import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferCompletedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferCompletedConsumer.class);

    private final ProcessTransferCompletedNotificationUseCase processTransferCompletedNotificationUseCase;

    public TransferCompletedConsumer(
            ProcessTransferCompletedNotificationUseCase processTransferCompletedNotificationUseCase
    ) {
        this.processTransferCompletedNotificationUseCase = processTransferCompletedNotificationUseCase;
    }

    @KafkaListener(
            topics = "transfer.completed",
            groupId = "notification-service",
            containerFactory = "transferCompletedKafkaListenerContainerFactory"
    )
    public void consume(TransferCompletedEvent event) {

        log.info(
                "[NOTIFICATION] TransferCompletedEvent received. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}",
                event.transferId(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount(),
                event.currency(),
                event.status()
        );

        processTransferCompletedNotificationUseCase.process(event);

        log.info(
                "[NOTIFICATION] TransferCompletedEvent processed successfully. transferId={}",
                event.transferId()
        );
    }
}