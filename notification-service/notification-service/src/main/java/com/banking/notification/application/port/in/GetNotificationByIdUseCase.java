package com.banking.notification.application.port.in;

import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationId;

public interface GetNotificationByIdUseCase {

    Notification getById(NotificationId notificationId);
}