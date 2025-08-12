package com.guarani.shopapi.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public class CreateOrderRequest {
    @NotEmpty
    private List<OrderItemRequest> items;

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
