package com.banking.account.domain.model;

import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        if (value == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }
}