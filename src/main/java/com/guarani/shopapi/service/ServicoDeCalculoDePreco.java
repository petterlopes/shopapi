package com.guarani.shopapi.service;

import com.guarani.shopapi.model.Order;
import com.guarani.shopapi.model.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ServicoDeCalculoDePreco {

    public void recalc(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem it : order.getItems()) {
            BigDecimal line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            subtotal = subtotal.add(line);
        }
        order.setSubtotal(subtotal);

        if (order.getDiscount() == null) order.setDiscount(BigDecimal.ZERO);
        if (order.getShippingFee() == null) order.setShippingFee(BigDecimal.ZERO);

        BigDecimal total = subtotal.subtract(order.getDiscount()).add(order.getShippingFee());
        order.setTotal(total.max(BigDecimal.ZERO));
    }
}
