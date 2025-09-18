package com.peritumct.shopapi.infrastructure.persistence.mapper;

import com.peritumct.shopapi.domain.user.Role;
import com.peritumct.shopapi.domain.user.User;
import com.peritumct.shopapi.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRole());
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        Role role = domain.getRole();
        return new UserEntity(domain.getId(), domain.getUsername(), domain.getPassword(), role);
    }
}
