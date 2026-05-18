package com.banking.notification.application.port.in;

import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;

public interface ProcessTransferCompletedNotificationUseCase {

    void process(TransferCompletedEvent event);
}