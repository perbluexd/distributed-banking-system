package com.banking.transaction.application.error;

import java.util.UUID;

public class AccountSnapshotNotFoundException extends RuntimeException {

    public AccountSnapshotNotFoundException(UUID accountId) {
        super("Account snapshot not found: " + accountId);
    }
}