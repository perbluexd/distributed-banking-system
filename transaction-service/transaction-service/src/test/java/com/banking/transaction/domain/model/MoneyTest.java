package com.banking.transaction.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = new Money(new BigDecimal("100.00"), Currency.PEN);

        assertEquals(new BigDecimal("100.00"), money.amount());
        assertEquals(Currency.PEN, money.currency());
    }

    @Test
    void shouldRejectNullAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(null, Currency.PEN));
    }

    @Test
    void shouldRejectNullCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("100.00"), null));
    }

    @Test
    void shouldRejectZeroAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.ZERO, Currency.PEN));
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-10.00"), Currency.PEN));
    }

    @Test
    void shouldValidateSameCurrency() {
        Money money = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money other = new Money(new BigDecimal("50.00"), Currency.PEN);

        assertDoesNotThrow(() -> money.validateSameCurrency(other));
    }

    @Test
    void shouldRejectDifferentCurrency() {
        Money money = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money other = new Money(new BigDecimal("50.00"), Currency.USD);

        assertThrows(IllegalArgumentException.class,
                () -> money.validateSameCurrency(other));
    }
}