package com.banking.notification.api.error;

import com.banking.notification.application.error.NotificationAlreadyProcessedException;
import com.banking.notification.application.error.NotificationNotFoundException;
import com.banking.notification.application.error.NotificationProcessingException;
import com.banking.notification.domain.exception.InvalidNotificationStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleNotificationNotFound() {
        var response = handler.handleNotificationNotFound(
                new NotificationNotFoundException("Notification not found")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertBody(
                response.getBody(),
                404,
                "NOTIFICATION_NOT_FOUND",
                "Notification not found"
        );
    }

    @Test
    void shouldHandleNotificationAlreadyProcessed() {
        var response = handler.handleNotificationAlreadyProcessed(
                new NotificationAlreadyProcessedException("Already processed")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertBody(
                response.getBody(),
                409,
                "NOTIFICATION_ALREADY_PROCESSED",
                "Already processed"
        );
    }

    @Test
    void shouldHandleNotificationProcessing() {
        var response = handler.handleNotificationProcessing(
                new NotificationProcessingException("Processing error")
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertBody(
                response.getBody(),
                500,
                "NOTIFICATION_PROCESSING_ERROR",
                "Processing error"
        );
    }

    @Test
    void shouldHandleInvalidNotificationState() {
        var response = handler.handleInvalidNotificationState(
                new InvalidNotificationStateException("Invalid state")
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertBody(
                response.getBody(),
                409,
                "INVALID_NOTIFICATION_STATE",
                "Invalid state"
        );
    }

    @Test
    void shouldHandleIllegalArgument() {
        var response = handler.handleIllegalArgument(
                new IllegalArgumentException("Invalid argument")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(
                response.getBody(),
                400,
                "INVALID_ARGUMENT",
                "Invalid argument"
        );
    }

    @Test
    void shouldHandleUnexpectedException() {
        var response = handler.handleUnexpected(
                new RuntimeException("Unexpected error")
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertBody(
                response.getBody(),
                500,
                "INTERNAL_SERVER_ERROR",
                "Unexpected error"
        );
    }

    private void assertBody(
            ApiErrorResponse body,
            int status,
            String code,
            String message
    ) {
        assertNotNull(body);
        assertNotNull(body.timestamp());
        assertEquals(status, body.status());
        assertEquals(code, body.code());
        assertEquals(message, body.message());
    }
}