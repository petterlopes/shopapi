package com.peritumct.shopapi.infrastructure.persistence.repository;

import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class OrderJpaRepositoryImpl implements OrderJpaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<OrderEntity> fetchDetailedById(Long id) {
        return fetchOrderWith("""
            select o from OrderEntity o
            left join fetch o.user
            left join fetch o.items it
            left join fetch it.product
            where o.id = :id
        """, id);
    }

    @Override
    public Optional<OrderEntity> fetchWithUser(Long id) {
        return fetchOrderWith("""
            select o from OrderEntity o
            left join fetch o.user
            where o.id = :id
        """, id);
    }

    private Optional<OrderEntity> fetchOrderWith(String jpql, Long id) {
        TypedQuery<OrderEntity> query = entityManager.createQuery(jpql, OrderEntity.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }
}
