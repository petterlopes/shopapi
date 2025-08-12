package com.guarani.shopapi.controller;

import com.guarani.shopapi.model.Product;
import com.guarani.shopapi.repository.IProductRepository;
import com.guarani.shopapi.service.spec.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products/search")
public class ProductSearchController {

    private final IProductRepository repo;

    public ProductSearchController(IProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<Product> search(@RequestParam(value="name", required = false) String name,
                                @RequestParam(value="category", required = false) String category,
                                @RequestParam(value="minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(value="maxPrice", required = false) BigDecimal maxPrice,
                                Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.filter(name, category, minPrice, maxPrice);
        return repo.findAll(spec, pageable);
    }
}
