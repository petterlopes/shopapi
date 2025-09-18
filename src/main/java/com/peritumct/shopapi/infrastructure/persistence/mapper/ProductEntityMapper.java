package com.peritumct.shopapi.infrastructure.persistence.mapper;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Product(entity.getId(), entity.getName(), entity.getDescription(), entity.getCategory(), entity.getPrice());
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        return new ProductEntity(domain.getId(), domain.getName(), domain.getDescription(), domain.getCategory(), domain.getPrice());
    }
}
