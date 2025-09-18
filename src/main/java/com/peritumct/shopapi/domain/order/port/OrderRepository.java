package com.peritumct.shopapi.domain.order.port;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    Optional<Order> findDetailedById(Long id);
    Optional<Order> findWithUser(Long id);
    PageResult<Order> search(OrderSearchFilters filters, PageRequest pageRequest);
    Order save(Order order);
    boolean existsById(Long id);
    void deleteById(Long id);
}
