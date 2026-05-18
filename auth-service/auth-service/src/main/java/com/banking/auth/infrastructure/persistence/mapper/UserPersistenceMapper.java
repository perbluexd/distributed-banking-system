package com.banking.auth.infrastructure.persistence.mapper;

import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;
import com.banking.auth.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    default UserEntity toEntity(User user) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                roles,
                user.getCreatedAt()
        );
    }

    default User toDomain(UserEntity entity) {
        Set<Role> roles = entity.getRoles()
                .stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());

        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                roles,
                entity.getCreatedAt()
        );
    }
}
