package com.banking.notification.application.port.out;

import com.banking.notification.domain.model.Notification;

public interface EmailSenderPort {

    void send(Notification notification);
}