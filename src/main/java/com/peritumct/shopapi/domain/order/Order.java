package com.peritumct.shopapi.domain.order;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final Long id;
    private final User user;
    private final LocalDateTime createdAt;
    private OrderStatus status;
    private final List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingFee;
    private BigDecimal total;

    public Order(Long id,
                 User user,
                 LocalDateTime createdAt,
                 OrderStatus status,
                 List<OrderItem> items,
                 BigDecimal subtotal,
                 BigDecimal discount,
                 BigDecimal shippingFee,
                 BigDecimal total) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
        this.status = status;
        this.items = new ArrayList<>(items);
        this.subtotal = subtotal;
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.total = total;
    }

    public static Order create(User user) {
        return new Order(null,
            user,
            LocalDateTime.now(),
            OrderStatus.PENDING,
            new ArrayList<>(),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO);
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void clearItems() {
        items.clear();
    }

    public void addItem(Product product, int quantity, BigDecimal unitPrice) {
        items.add(new OrderItem(product, quantity, unitPrice));
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
