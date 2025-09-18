package com.peritumct.shopapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peritumct.shopapi.model.Product;
import com.peritumct.shopapi.repository.IProductRepository;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.spec.ProductSpecifications;
import com.peritumct.shopapi.service.usecase.ProductUseCase;

@Service
public class ProductApplicationService implements ProductUseCase {

    private final IProductRepository productRepository;

    public ProductApplicationService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto nao encontrado: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String name,
                                        String category,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.filter(name, category, minPrice, maxPrice);
        return productRepository.findAll(spec, pageable);
    }
}
