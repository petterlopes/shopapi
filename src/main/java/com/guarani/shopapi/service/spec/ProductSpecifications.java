package com.guarani.shopapi.service.spec;

import com.guarani.shopapi.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> filter(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                preds.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }
            if (minPrice != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}
