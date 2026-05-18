package com.banking.gateway.security;

import java.util.List;
import java.util.UUID;

public record JwtUser(
        UUID userId,
        String email,
        List<String> roles
) {
}