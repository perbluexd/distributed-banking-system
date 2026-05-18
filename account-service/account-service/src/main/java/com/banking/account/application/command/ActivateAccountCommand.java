package com.banking.account.application.command;

import java.util.UUID;

public record ActivateAccountCommand(
        UUID accountId
) {
}