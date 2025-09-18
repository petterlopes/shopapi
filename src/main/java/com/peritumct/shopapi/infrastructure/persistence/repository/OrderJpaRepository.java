package com.peritumct.shopapi.infrastructure.persistence.repository;

import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity>, OrderJpaRepositoryCustom {
}
