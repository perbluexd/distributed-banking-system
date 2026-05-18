package com.banking.gateway.security;

import com.banking.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
public class JwtService {

    private final JwtConfig jwtConfig;
    private final SecretKey key;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public JwtUser validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .requireIssuer(jwtConfig.getIssuer())
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object type = claims.get("type");
            if (type == null || !"access".equals(type.toString())) {
                throw new JwtException("Invalid token type");
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new JwtException("Missing subject");
            }

            String email = claims.get("email", String.class);

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            return new JwtUser(
                    UUID.fromString(subject),
                    email,
                    roles == null ? List.of() : roles
            );

        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidJwtException("Invalid access token");
        }
    }
}