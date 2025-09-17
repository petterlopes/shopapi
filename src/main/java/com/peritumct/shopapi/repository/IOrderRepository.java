package com.peritumct.shopapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peritumct.shopapi.model.Order;

import java.util.Optional;


public interface IOrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("""
        select o from Order o
        left join fetch o.user
        left join fetch o.items it
        left join fetch it.product
        where o.id = :id
    """)
    Optional<Order> findByIdWithAll(@Param("id") Long id);

    @Query("""
        select o from Order o
        left join fetch o.user
        where o.id = :id
    """)
    Optional<Order> findByIdWithUser(@Param("id") Long id);
}
