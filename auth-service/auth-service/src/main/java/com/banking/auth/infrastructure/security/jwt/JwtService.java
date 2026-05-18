package com.banking.auth.infrastructure.security.jwt;

import com.banking.auth.application.port.out.JwtTokenPort;
import com.banking.auth.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtService implements JwtTokenPort {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(User user) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getAccessTtlSeconds());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("email", user.getEmail())
                .claim("type", "access")
                .claim("roles", user.getRoles().stream().map(Enum::name).collect(Collectors.toList()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getRefreshTtlSeconds());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuer(properties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
