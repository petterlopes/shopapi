package com.peritumct.shopapi.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private final Long id;
    private final Long orderId;
    private final PaymentMethod method;
    private final PaymentStatus status;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;

    public Payment(Long id,
                   Long orderId,
                   PaymentMethod method,
                   PaymentStatus status,
                   BigDecimal amount,
                   LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Payment withStatus(PaymentStatus newStatus) {
        return new Payment(id, orderId, method, newStatus, amount, createdAt);
    }
}
