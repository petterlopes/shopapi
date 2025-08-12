package com.guarani.shopapi.dto;

import com.guarani.shopapi.model.OrderStatus;

public class OrderStatusUpdateRequest {
    private OrderStatus status;
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
