package com.banking.account.application.error;

public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}