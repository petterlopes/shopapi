package com.peritumct.shopapi.dto;

import com.peritumct.shopapi.domain.order.OrderStatus;

public class OrderStatusUpdateRequest {
    private OrderStatus status;
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
