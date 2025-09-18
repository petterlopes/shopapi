package com.peritumct.shopapi.infrastructure.persistence.adapter;

import com.peritumct.shopapi.domain.user.User;
import com.peritumct.shopapi.domain.user.port.UserRepository;
import com.peritumct.shopapi.infrastructure.persistence.entity.UserEntity;
import com.peritumct.shopapi.infrastructure.persistence.mapper.UserEntityMapper;
import com.peritumct.shopapi.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper mapper;

    public JpaUserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                    UserEntityMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
