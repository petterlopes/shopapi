package com.peritumct.shopapi.domain.user.port;

import com.peritumct.shopapi.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User save(User user);
}
