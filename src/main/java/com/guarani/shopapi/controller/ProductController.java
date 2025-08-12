package com.guarani.shopapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.guarani.shopapi.model.Product;
import com.guarani.shopapi.repository.IProductRepository;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final IProductRepository repo;

    public ProductController(IProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Product> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PostMapping
    public Product create(@Valid @RequestBody Product p) {
        return repo.save(p);
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product p) {
        return repo.findById(id).map(existing -> {
            existing.setName(p.getName());
            existing.setDescription(p.getDescription());
            existing.setPrice(p.getPrice());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
