package com.peritumct.shopapi.service.usecase;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peritumct.shopapi.model.Product;

public interface ProductUseCase {

    List<Product> listProducts();

    Product getProduct(Long id);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    Page<Product> searchProducts(String name,
                                 String category,
                                 BigDecimal minPrice,
                                 BigDecimal maxPrice,
                                 Pageable pageable);
}
