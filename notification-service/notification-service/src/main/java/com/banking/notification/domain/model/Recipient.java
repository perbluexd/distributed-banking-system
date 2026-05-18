package com.banking.notification.domain.model;

import java.util.Objects;

public record Recipient(
        String email
) {

    public Recipient {
        Objects.requireNonNull(email, "Recipient email cannot be null");

        if (email.isBlank()) {
            throw new IllegalArgumentException("Recipient email cannot be blank");
        }
    }
}