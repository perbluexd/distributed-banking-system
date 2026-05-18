package com.banking.auth.api.error;

import java.time.Instant;

public class ApiError {

    private final String code;
    private final String message;
    private final Instant timestamp;
    private final String path;

    public ApiError(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}