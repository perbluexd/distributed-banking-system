package com.banking.transaction.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TransferId(UUID value) {

    public TransferId {
        if (value == null) {
            throw new IllegalArgumentException("TransferId cannot be null");
        }
    }

    public static TransferId newId() {
        return new TransferId(UUID.randomUUID());
    }
}