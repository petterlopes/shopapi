package com.peritumct.shopapi.service.usecase;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.domain.order.OrderStatus;
import com.peritumct.shopapi.domain.order.OrderUpdateCommand;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;

public interface OrderUseCase {

    Order loadOrder(Long id);

    PageResult<Order> searchOrders(OrderSearchFilters filters, PageRequest pageRequest);

    void updateOrder(Long id, OrderUpdateCommand command);

    void updateStatus(Long id, OrderStatus status);

    void delete(Long id);
}
