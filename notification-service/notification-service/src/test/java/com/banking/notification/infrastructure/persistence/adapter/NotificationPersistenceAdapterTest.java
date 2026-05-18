package com.banking.notification.infrastructure.persistence.adapter;

import com.banking.notification.domain.model.*;
import com.banking.notification.infrastructure.persistence.repository.NotificationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPersistenceAdapterTest {

    private NotificationPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        NotificationJpaRepository repository =
                org.mockito.Mockito.mock(NotificationJpaRepository.class);

        adapter = new NotificationPersistenceAdapter(repository);
    }

    @Test
    void shouldReturnSameNotificationWhenSaving() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        Notification saved = adapter.save(notification);

        assertSame(notification, saved);
    }
}