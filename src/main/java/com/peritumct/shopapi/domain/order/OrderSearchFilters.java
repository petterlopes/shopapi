package com.peritumct.shopapi.domain.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderSearchFilters(OrderStatus status,
                                 LocalDate fromDate,
                                 LocalDate toDate,
                                 BigDecimal minTotal,
                                 BigDecimal maxTotal) {
}
