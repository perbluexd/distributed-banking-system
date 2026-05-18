package com.banking.auth.infrastructure.security.jwt;

import com.banking.auth.application.error.ErrorCode;
import com.banking.auth.application.error.UnauthorizedException;
import com.banking.auth.application.port.out.JwtTokenVerifierPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtTokenVerifier implements JwtTokenVerifierPort {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtTokenVerifier(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UUID verifyRefreshTokenAndGetUserId(String refreshToken) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(properties.getIssuer())
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);

            Claims claims = jws.getBody();

            Object type = claims.get("type");
            if (type == null || !"refresh".equals(type.toString())) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
            }

            return UUID.fromString(subject);

        } catch (JwtException | IllegalArgumentException ex) {
            // JwtException cubre: expired, signature invalid, malformed, etc.
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    @Override
    public UUID verifyAccessTokenAndGetUserId(String accessToken) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer(properties.getIssuer())
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);

            Claims claims = jws.getBody();

            Object type = claims.get("type");
            if (type == null || !"access".equals(type.toString())) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid access token");
            }

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid access token");
            }

            return UUID.fromString(subject);

        } catch (JwtException | IllegalArgumentException ex) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED, "Invalid access token");
        }
    }

}
