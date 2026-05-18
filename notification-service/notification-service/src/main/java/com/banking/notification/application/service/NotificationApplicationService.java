package com.banking.notification.application.service;

import com.banking.notification.application.error.NotificationAlreadyProcessedException;
import com.banking.notification.application.error.NotificationProcessingException;
import com.banking.notification.application.port.in.ProcessTransferCompletedNotificationUseCase;
import com.banking.notification.application.port.in.ProcessTransferFailedNotificationUseCase;
import com.banking.notification.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.notification.application.port.out.EmailSenderPort;
import com.banking.notification.application.port.out.NotificationRepositoryPort;
import com.banking.notification.domain.model.*;
import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;
import com.banking.notification.infrastructure.messaging.event.TransferFailedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationApplicationService implements
        ProcessTransferCompletedNotificationUseCase,
        ProcessTransferFailedNotificationUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;
    private final CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;
    private final EmailSenderPort emailSenderPort;

    public NotificationApplicationService(
            NotificationRepositoryPort notificationRepositoryPort,
            CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort,
            EmailSenderPort emailSenderPort
    ) {
        this.notificationRepositoryPort = notificationRepositoryPort;
        this.customerSnapshotRepositoryPort = customerSnapshotRepositoryPort;
        this.emailSenderPort = emailSenderPort;
    }

    @Override
    @Transactional
    public void process(TransferCompletedEvent event) {
        processNotification(
                event.transferId(),
                event.customerId(),
                NotificationType.TRANSFER_COMPLETED
        );
    }

    @Override
    @Transactional
    public void process(TransferFailedEvent event) {
        processNotification(
                event.transferId(),
                event.customerId(),
                NotificationType.TRANSFER_FAILED
        );
    }

    private void processNotification(
            UUID transferId,
            UUID customerId,
            NotificationType type
    ) {
        if (notificationRepositoryPort.existsByTransferIdAndType(transferId, type)) {
            throw new NotificationAlreadyProcessedException(
                    "Notification already processed for transferId=%s and type=%s"
                            .formatted(transferId, type)
            );
        }

        CustomerSnapshot customerSnapshot = customerSnapshotRepositoryPort
                .findByCustomerId(CustomerId.of(customerId))
                .orElseThrow(() -> new NotificationProcessingException(
                        "Customer snapshot not found for customerId=%s".formatted(customerId)
                ));

        Notification notification = Notification.create(
                transferId,
                type,
                NotificationChannel.EMAIL,
                new Recipient(customerSnapshot.getEmail())
        );

        notificationRepositoryPort.save(notification);

        try {
            emailSenderPort.send(notification);

            notification.markAsSent();
            notificationRepositoryPort.save(notification);

        } catch (Exception ex) {
            notification.markAsFailed(
                    ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()
            );
            notificationRepositoryPort.save(notification);

            throw new NotificationProcessingException(
                    "Error processing notification",
                    ex
            );
        }
    }
}