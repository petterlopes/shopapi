package com.guarani.shopapi.dto;

import java.math.BigDecimal;
import java.util.List;

public class UpdateOrderRequest {
    public static class Item {
        private Long productId;
        private int quantity;
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    private List<Item> items;
    private BigDecimal discount;
    private BigDecimal shippingFee;

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }
}
