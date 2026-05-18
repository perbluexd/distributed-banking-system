package com.banking.account.application.query;

import java.util.UUID;

public record GetAccountsByCustomerIdQuery(
        UUID customerId
) {
}