package com.peritumct.shopapi.infrastructure.persistence.repository;

import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;

import java.util.Optional;

public interface OrderJpaRepositoryCustom {
    Optional<OrderEntity> fetchDetailedById(Long id);
    Optional<OrderEntity> fetchWithUser(Long id);
}
