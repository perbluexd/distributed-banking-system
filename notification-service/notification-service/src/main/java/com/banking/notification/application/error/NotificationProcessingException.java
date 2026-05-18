package com.banking.notification.application.error;

public class NotificationProcessingException extends RuntimeException {

    public NotificationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationProcessingException(String message) {
        super(message);
    }
}