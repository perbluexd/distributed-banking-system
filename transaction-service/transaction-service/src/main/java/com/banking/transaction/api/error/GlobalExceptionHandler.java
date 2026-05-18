package com.banking.transaction.api.error;

import com.banking.transaction.application.error.AccountSnapshotNotFoundException;
import com.banking.transaction.application.error.DuplicateTransferException;
import com.banking.transaction.application.error.ExternalServiceException;
import com.banking.transaction.application.error.InsufficientFundsException;
import com.banking.transaction.application.error.InvalidTransferException;
import com.banking.transaction.application.error.TransferNotFoundException;
import com.banking.transaction.infrastructure.client.exception.AccountCommandFailedException;
import com.banking.transaction.infrastructure.client.exception.AccountServiceException;
import com.banking.transaction.infrastructure.client.exception.AccountServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTransferNotFound(
            TransferNotFoundException ex
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "TRANSFER_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountSnapshotNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountSnapshotNotFound(
            AccountSnapshotNotFoundException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "ACCOUNT_SNAPSHOT_NOT_FOUND",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicateTransferException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateTransfer(
            DuplicateTransferException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "DUPLICATE_TRANSFER",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidTransfer(
            InvalidTransferException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_TRANSFER",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientFunds(
            InsufficientFundsException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "INSUFFICIENT_FUNDS",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountServiceUnavailable(
            AccountServiceUnavailableException ex
    ) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "ACCOUNT_SERVICE_UNAVAILABLE",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountCommandFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountCommandFailed(
            AccountCommandFailedException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "ACCOUNT_COMMAND_FAILED",
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountServiceException(
            AccountServiceException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "ACCOUNT_SERVICE_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalService(
            ExternalServiceException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "EXTERNAL_SERVICE_ERROR",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation error");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                message
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
            IllegalStateException ex
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "BUSINESS_CONFLICT",
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex
    ) {
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