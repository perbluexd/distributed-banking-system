package com.banking.auth.infrastructure.persistence.adapter;

import com.banking.auth.application.port.out.UserRepositoryPort;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.banking.auth.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    public UserPersistenceAdapter(UserJpaRepository repository,
                                  UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(user)
                )
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::toDomain);
    }
    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

}
