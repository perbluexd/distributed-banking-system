package com.banking.auth.application.port.out;

import com.banking.auth.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

}
