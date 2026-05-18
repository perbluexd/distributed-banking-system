package com.banking.notification.application.port.in;

import com.banking.notification.infrastructure.messaging.event.TransferFailedEvent;

public interface ProcessTransferFailedNotificationUseCase {

    void process(TransferFailedEvent event);
}