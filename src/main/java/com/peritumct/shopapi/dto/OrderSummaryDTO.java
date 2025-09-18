package com.peritumct.shopapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.peritumct.shopapi.domain.order.OrderStatus;

public class OrderSummaryDTO {
    private Long id;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal total;

    public OrderSummaryDTO(Long id, LocalDateTime createdAt, OrderStatus status, BigDecimal total) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.total = total;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
}
