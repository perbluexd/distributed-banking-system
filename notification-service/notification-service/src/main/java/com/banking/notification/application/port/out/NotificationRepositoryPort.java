package com.banking.notification.application.port.out;

import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationId;
import com.banking.notification.domain.model.NotificationType;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    Optional<Notification> findById(NotificationId notificationId);

    Optional<Notification> findByTransferIdAndType(
            UUID transferId,
            NotificationType type
    );

    boolean existsByTransferIdAndType(
            UUID transferId,
            NotificationType type
    );
}