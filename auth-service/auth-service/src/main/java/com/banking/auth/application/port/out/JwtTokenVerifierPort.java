package com.banking.auth.application.port.out;

import java.util.UUID;

public interface JwtTokenVerifierPort {

    /**
     * Valida firma/issuer/exp del refresh token y retorna el subject como UUID (userId).
     * Si no es válido, debe lanzar excepción (en infra) o devolver error (según implementación).
     */
    UUID verifyRefreshTokenAndGetUserId(String refreshToken);
    UUID verifyAccessTokenAndGetUserId(String accessToken); // nuevo
}
