package com.guarani.shopapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private Order orderRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {}

    public Payment(Order orderRef, PaymentMethod method, BigDecimal amount) {
        this.orderRef = orderRef;
        this.method = method;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public Long getId() { return id; }
    public Order getOrderRef() { return orderRef; }
    public void setOrderRef(Order orderRef) { this.orderRef = orderRef; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
