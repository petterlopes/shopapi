package com.peritumct.shopapi.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peritumct.shopapi.model.Product;
import com.peritumct.shopapi.service.usecase.ProductUseCase;

@RestController
@RequestMapping("/api/products/search")
public class ProductSearchController {

    private final ProductUseCase productUseCase;

    public ProductSearchController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public Page<Product> search(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "category", required = false) String category,
                                @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                Pageable pageable) {
        return productUseCase.searchProducts(name, category, minPrice, maxPrice, pageable);
    }
}
