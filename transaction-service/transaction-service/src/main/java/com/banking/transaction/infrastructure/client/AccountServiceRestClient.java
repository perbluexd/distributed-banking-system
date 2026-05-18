package com.banking.transaction.infrastructure.client;

import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.infrastructure.client.exception.AccountCommandFailedException;
import com.banking.transaction.infrastructure.client.exception.AccountServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountServiceRestClient implements AccountServiceClient {

    private final RestClient restClient;

    public AccountServiceRestClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.account.base-url}") String accountServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(accountServiceBaseUrl)
                .build();
    }

    @Override
    public void debit(UUID accountId, BigDecimal amount, Currency currency, UUID transferId) {
        AccountMoneyOperationRequest request = new AccountMoneyOperationRequest(
                amount,
                currency,
                transferId
        );

        try {
            restClient.post()
                    .uri("/accounts/{accountId}/debit", accountId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException ex) {
            throw new AccountCommandFailedException(
                    "Debit command failed for account: " + accountId,
                    ex
            );
        } catch (RestClientException ex) {
            throw new AccountServiceUnavailableException(
                    "Account service unavailable during debit",
                    ex
            );
        }
    }

    @Override
    public void credit(UUID accountId, BigDecimal amount, Currency currency, UUID transferId) {
        AccountMoneyOperationRequest request = new AccountMoneyOperationRequest(
                amount,
                currency,
                transferId
        );

        try {
            restClient.post()
                    .uri("/accounts/{accountId}/credit", accountId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpStatusCodeException ex) {
            throw new AccountCommandFailedException(
                    "Credit command failed for account: " + accountId,
                    ex
            );
        } catch (RestClientException ex) {
            throw new AccountServiceUnavailableException(
                    "Account service unavailable during credit",
                    ex
            );
        }
    }

    private record AccountMoneyOperationRequest(
            BigDecimal amount,
            Currency currency,
            UUID transferId
    ) {
    }
}