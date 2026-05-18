package com.banking.auth.infrastructure.security.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Sha256RefreshTokenHasherTest {

    private final Sha256RefreshTokenHasher hasher = new Sha256RefreshTokenHasher();

    @Test
    void shouldHashToken() {

        String token = "refresh-token-example";

        String hash = hasher.hash(token);

        assertNotNull(hash);
        assertFalse(hash.isBlank());
    }

    @Test
    void sameTokenShouldProduceSameHash() {

        String token = "refresh-token-example";

        String hash1 = hasher.hash(token);
        String hash2 = hasher.hash(token);

        assertEquals(hash1, hash2);
    }

    @Test
    void differentTokensShouldProduceDifferentHashes() {

        String token1 = "refresh-token-1";
        String token2 = "refresh-token-2";

        String hash1 = hasher.hash(token1);
        String hash2 = hasher.hash(token2);

        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashShouldHaveExpectedLength() {

        String token = "refresh-token-example";

        String hash = hasher.hash(token);

        // SHA-256 produce 64 caracteres hex
        assertEquals(64, hash.length());
    }

}