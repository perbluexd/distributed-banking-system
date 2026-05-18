package com.banking.notification.application.service;

import com.banking.notification.application.error.NotificationNotFoundException;
import com.banking.notification.application.port.in.GetNotificationByIdUseCase;
import com.banking.notification.application.port.out.NotificationRepositoryPort;
import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationByIdService implements GetNotificationByIdUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public GetNotificationByIdService(
            NotificationRepositoryPort notificationRepositoryPort
    ) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getById(NotificationId notificationId) {
        return notificationRepositoryPort
                .findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification not found: " + notificationId.value()
                        )
                );
    }
}