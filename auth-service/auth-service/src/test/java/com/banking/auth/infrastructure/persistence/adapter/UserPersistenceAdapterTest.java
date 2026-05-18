package com.banking.auth.infrastructure.persistence.adapter;

import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.persistence.entity.UserEntity;
import com.banking.auth.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.banking.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserPersistenceMapper userPersistenceMapper;

    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserPersistenceAdapter(userJpaRepository, userPersistenceMapper);
    }

    @Test
    void shouldSaveUser() {
        UUID userId = UUID.randomUUID();

        User domainUser = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );

        UserEntity entityToSave = mock(UserEntity.class);
        UserEntity savedEntity = mock(UserEntity.class);
        User mappedBack = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );

        when(userPersistenceMapper.toEntity(domainUser)).thenReturn(entityToSave);
        when(userJpaRepository.save(entityToSave)).thenReturn(savedEntity);
        when(userPersistenceMapper.toDomain(savedEntity)).thenReturn(mappedBack);

        User result = adapter.save(domainUser);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user@mail.com", result.getEmail());

        verify(userPersistenceMapper).toEntity(domainUser);
        verify(userJpaRepository).save(entityToSave);
        verify(userPersistenceMapper).toDomain(savedEntity);
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "user@mail.com";
        UserEntity entity = mock(UserEntity.class);
        User domainUser = new User(
                UUID.randomUUID(),
                email,
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );

        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(userPersistenceMapper.toDomain(entity)).thenReturn(domainUser);

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());

        verify(userJpaRepository).findByEmail(email);
        verify(userPersistenceMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenUserByEmailDoesNotExist() {
        String email = "missing@mail.com";

        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail(email);

        assertTrue(result.isEmpty());

        verify(userJpaRepository).findByEmail(email);
        verify(userPersistenceMapper, never()).toDomain(any());
    }

    @Test
    void shouldFindUserById() {
        UUID userId = UUID.randomUUID();
        UserEntity entity = mock(UserEntity.class);
        User domainUser = new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(userPersistenceMapper.toDomain(entity)).thenReturn(domainUser);

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());

        verify(userJpaRepository).findById(userId);
        verify(userPersistenceMapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenUserByIdDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isEmpty());

        verify(userJpaRepository).findById(userId);
        verify(userPersistenceMapper, never()).toDomain(any());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        String email = "user@mail.com";

        when(userJpaRepository.existsByEmail(email)).thenReturn(true);

        boolean result = adapter.existsByEmail(email);

        assertTrue(result);
        verify(userJpaRepository).existsByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        String email = "missing@mail.com";

        when(userJpaRepository.existsByEmail(email)).thenReturn(false);

        boolean result = adapter.existsByEmail(email);

        assertFalse(result);
        verify(userJpaRepository).existsByEmail(email);
    }
}