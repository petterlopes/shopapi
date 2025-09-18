package com.peritumct.shopapi.domain.order.service;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderItem;

import java.math.BigDecimal;

public class PriceCalculator {

    public void recalculate(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            subtotal = subtotal.add(item.getLineTotal());
        }

        BigDecimal discount = order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setShippingFee(shipping);

        BigDecimal total = subtotal.subtract(discount).add(shipping);
        order.setTotal(total.max(BigDecimal.ZERO));
    }
}
