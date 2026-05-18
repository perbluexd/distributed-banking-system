package com.banking.notification.api.error;

import com.banking.notification.application.error.NotificationAlreadyProcessedException;
import com.banking.notification.application.error.NotificationNotFoundException;
import com.banking.notification.application.error.NotificationProcessingException;
import com.banking.notification.domain.exception.InvalidNotificationStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationNotFound(
            NotificationNotFoundException ex
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOTIFICATION_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(NotificationAlreadyProcessedException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationAlreadyProcessed(
            NotificationAlreadyProcessedException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "NOTIFICATION_ALREADY_PROCESSED",
                ex.getMessage()
        );
    }

    @ExceptionHandler(NotificationProcessingException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationProcessing(
            NotificationProcessingException ex
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "NOTIFICATION_PROCESSING_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidNotificationStateException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidNotificationState(
            InvalidNotificationStateException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "INVALID_NOTIFICATION_STATE",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_ARGUMENT",
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        ex.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                code,
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}