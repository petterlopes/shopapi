package com.peritumct.shopapi.domain.order;

import com.peritumct.shopapi.domain.product.Product;

import java.math.BigDecimal;

public class OrderItem {
    private final Product product;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Product product, int quantity, BigDecimal unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
