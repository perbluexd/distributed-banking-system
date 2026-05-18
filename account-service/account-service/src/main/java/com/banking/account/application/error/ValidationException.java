package com.banking.account.application.error;

public class ValidationException extends BusinessException {

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}