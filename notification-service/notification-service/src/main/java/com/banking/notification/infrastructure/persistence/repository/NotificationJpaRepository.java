package com.banking.notification.infrastructure.persistence.repository;

import com.banking.notification.domain.model.NotificationType;
import com.banking.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

    Optional<NotificationJpaEntity> findByTransferIdAndType(
            UUID transferId,
            NotificationType type
    );

    boolean existsByTransferIdAndType(
            UUID transferId,
            NotificationType type
    );
}