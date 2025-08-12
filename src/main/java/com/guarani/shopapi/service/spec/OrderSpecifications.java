package com.guarani.shopapi.service.spec;

import com.guarani.shopapi.model.Order;
import com.guarani.shopapi.model.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> filter(OrderStatus status,
                                              LocalDateTime from,
                                              LocalDateTime to,
                                              BigDecimal minTotal,
                                              BigDecimal maxTotal) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (status != null) {
                preds.add(cb.equal(root.get("status"), status));
            }
            if (from != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (to != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }
            if (minTotal != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("total"), minTotal));
            }
            if (maxTotal != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("total"), maxTotal));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
