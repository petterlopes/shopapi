package com.peritumct.shopapi.service.usecase;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;

import java.math.BigDecimal;
import java.util.List;

public interface ProductUseCase {

    List<Product> listProducts();

    Product getProduct(Long id);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    PageResult<Product> searchProducts(String name,
                                       String category,
                                       BigDecimal minPrice,
                                       BigDecimal maxPrice,
                                       PageRequest pageRequest);
}
