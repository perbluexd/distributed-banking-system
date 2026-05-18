package com.banking.account.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void shouldCreateMoneySuccessfully() {
        Money money = new Money(new BigDecimal("100.00"), Currency.PEN);

        assertEquals(new BigDecimal("100.00"), money.amount());
        assertEquals(Currency.PEN, money.currency());
    }

    @Test
    void shouldCreateZeroMoney() {
        Money money = Money.zero(Currency.USD);

        assertEquals(new BigDecimal("0.00"), money.amount());
        assertEquals(Currency.USD, money.currency());
    }

    @Test
    void shouldAddMoneyWithSameCurrency() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money second = new Money(new BigDecimal("50.00"), Currency.PEN);

        Money result = first.add(second);

        assertEquals(new BigDecimal("150.00"), result.amount());
        assertEquals(Currency.PEN, result.currency());
    }

    @Test
    void shouldSubtractMoneyWithSameCurrency() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money second = new Money(new BigDecimal("40.00"), Currency.PEN);

        Money result = first.subtract(second);

        assertEquals(new BigDecimal("60.00"), result.amount());
        assertEquals(Currency.PEN, result.currency());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-10.00"), Currency.PEN)
        );
    }

    @Test
    void shouldThrowExceptionWhenCurrenciesAreDifferent() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money second = new Money(new BigDecimal("50.00"), Currency.USD);

        assertThrows(IllegalArgumentException.class, () -> first.add(second));
    }

    @Test
    void shouldThrowExceptionWhenSubtractingMoreThanBalance() {
        Money first = new Money(new BigDecimal("100.00"), Currency.PEN);
        Money second = new Money(new BigDecimal("150.00"), Currency.PEN);

        assertThrows(IllegalArgumentException.class, () -> first.subtract(second));
    }
}