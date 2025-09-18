package com.peritumct.shopapi.controller;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.dto.ProductResponse;
import com.peritumct.shopapi.service.usecase.ProductUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products/search")
public class ProductSearchController {

    private final ProductUseCase productUseCase;

    public ProductSearchController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public Page<ProductResponse> search(@RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "category", required = false) String category,
                                        @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                        Pageable pageable) {
        PageResult<Product> result = productUseCase.searchProducts(name, category, minPrice, maxPrice, new PageRequest(pageable.getPageNumber(), pageable.getPageSize()));
        List<ProductResponse> responses = result.items().stream()
            .map(product -> new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getCategory(), product.getPrice()))
            .toList();
        return new PageImpl<>(responses, pageable, result.totalElements());
    }
}
