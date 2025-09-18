package com.peritumct.shopapi.domain.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderUpdateCommand(List<OrderUpdateItem> items,
                                 BigDecimal discount,
                                 BigDecimal shippingFee) {
}
