package com.banking.auth.application.error;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
