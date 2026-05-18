package com.banking.transaction.infrastructure.client;

import com.banking.transaction.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountServiceClient {

    void debit(UUID accountId, BigDecimal amount, Currency currency, UUID transferId);

    void credit(UUID accountId, BigDecimal amount, Currency currency, UUID transferId);
}