package com.banking.auth.api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RefreshRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenRequestIsValid() throws Exception {
        RefreshRequest request = new RefreshRequest();
        setField(request, "refreshToken", "refresh-token-value");

        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenRefreshTokenIsBlank() throws Exception {
        RefreshRequest request = new RefreshRequest();
        setField(request, "refreshToken", "");

        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("refreshToken")));
    }

    @Test
    void shouldFailWhenRefreshTokenIsNull() {
        RefreshRequest request = new RefreshRequest();

        Set<ConstraintViolation<RefreshRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("refreshToken")));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}