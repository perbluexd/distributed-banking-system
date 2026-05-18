package com.banking.notification.infrastructure.persistence.adapter;

import com.banking.notification.application.port.out.NotificationRepositoryPort;
import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationId;
import com.banking.notification.domain.model.NotificationType;
import com.banking.notification.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import com.banking.notification.infrastructure.persistence.repository.NotificationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository notificationJpaRepository;

    public NotificationPersistenceAdapter(
            NotificationJpaRepository notificationJpaRepository
    ) {
        this.notificationJpaRepository = notificationJpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        notificationJpaRepository.save(
                NotificationPersistenceMapper.toEntity(notification)
        );

        return notification;
    }

    @Override
    public Optional<Notification> findById(NotificationId notificationId) {
        return notificationJpaRepository
                .findById(notificationId.value())
                .map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Notification> findByTransferIdAndType(
            UUID transferId,
            NotificationType type
    ) {
        return notificationJpaRepository
                .findByTransferIdAndType(transferId, type)
                .map(NotificationPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByTransferIdAndType(
            UUID transferId,
            NotificationType type
    ) {
        return notificationJpaRepository.existsByTransferIdAndType(
                transferId,
                type
        );
    }
}