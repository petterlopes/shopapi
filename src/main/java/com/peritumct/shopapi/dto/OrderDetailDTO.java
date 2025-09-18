package com.peritumct.shopapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.peritumct.shopapi.domain.order.OrderStatus;

public class OrderDetailDTO {
    private Long id;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal total;
    private String username;
    private List<OrderItemDTO> items;

    public OrderDetailDTO(Long id, LocalDateTime createdAt, OrderStatus status,
                          BigDecimal subtotal, BigDecimal discount, BigDecimal shippingFee,
                          BigDecimal total, String username, List<OrderItemDTO> items) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.total = total;
        this.username = username;
        this.items = items;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public BigDecimal getTotal() { return total; }
    public String getUsername() { return username; }
    public List<OrderItemDTO> getItems() { return items; }
}
