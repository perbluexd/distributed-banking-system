package com.banking.notification.application.error;

public class NotificationAlreadyProcessedException extends RuntimeException {

    public NotificationAlreadyProcessedException(String message) {
        super(message);
    }
}