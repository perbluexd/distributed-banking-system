package com.banking.account.application.error;

public class ConflictException extends BusinessException {

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}