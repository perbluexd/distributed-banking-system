package com.banking.account.api.dto;

import com.banking.account.domain.model.AccountStatus;

import java.util.UUID;

public record ChangeAccountStatusResponse(
        UUID accountId,
        AccountStatus status,
        String message
) {
}