package com.banking.auth.application.error;

public enum ErrorCode {

    INVALID_REQUEST,
    INVALID_EMAIL,
    WEAK_PASSWORD,

    EMAIL_ALREADY_EXISTS,

    UNAUTHORIZED,
    USER_NOT_FOUND,
    INVALID_REFRESH_TOKEN,

    INTERNAL_ERROR
}