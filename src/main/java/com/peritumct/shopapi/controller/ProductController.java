package com.peritumct.shopapi.controller;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.dto.ProductRequest;
import com.peritumct.shopapi.dto.ProductResponse;
import com.peritumct.shopapi.service.usecase.ProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public List<ProductResponse> list() {
        return productUseCase.listProducts().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(productUseCase.getProduct(id)));
    }

    @PreAuthorize("@securityExpressions.hasStaffPrivileges(authentication)")
    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        Product product = productUseCase.createProduct(toDomain(null, request));
        return toResponse(product);
    }

    @PreAuthorize("@securityExpressions.hasStaffPrivileges(authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        Product updated = productUseCase.updateProduct(id, toDomain(id, request));
        return ResponseEntity.ok(toResponse(updated));
    }

    @PreAuthorize("@securityExpressions.hasStaffPrivileges(authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private Product toDomain(Long id, ProductRequest request) {
        return new Product(id, request.getName(), request.getDescription(), request.getCategory(), request.getPrice());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getCategory(), product.getPrice());
    }
}
