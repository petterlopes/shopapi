package com.peritumct.shopapi.service.spec;

import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> filter(OrderSearchFilters filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters == null) {
                return cb.and();
            }

            if (filters.status() != null) {
                predicates.add(cb.equal(root.get("status"), filters.status()));
            }

            LocalDate fromDate = filters.fromDate();
            LocalDate toDate = filters.toDate();
            if (fromDate != null) {
                LocalDateTime from = fromDate.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (toDate != null) {
                LocalDateTime to = toDate.atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            }

            BigDecimal minTotal = filters.minTotal();
            BigDecimal maxTotal = filters.maxTotal();
            if (minTotal != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("total"), minTotal));
            }
            if (maxTotal != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("total"), maxTotal));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
