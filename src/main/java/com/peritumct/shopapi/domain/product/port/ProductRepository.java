package com.peritumct.shopapi.domain.product.port;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Product save(Product product);
    void deleteById(Long id);
    boolean existsById(Long id);
    PageResult<Product> search(String name,
                               String category,
                               BigDecimal minPrice,
                               BigDecimal maxPrice,
                               PageRequest pageRequest);
}
