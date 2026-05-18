package com.banking.account.application.query;

import java.util.UUID;

public record GetAccountByIdQuery(
        UUID accountId
) {
}