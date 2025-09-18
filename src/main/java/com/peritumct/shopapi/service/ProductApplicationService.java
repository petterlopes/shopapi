package com.peritumct.shopapi.service;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.product.port.ProductRepository;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.usecase.ProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductApplicationService implements ProductUseCase {

    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository) {
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
        Product updated = new Product(existing.getId(), product.getName(), product.getDescription(), product.getCategory(), product.getPrice());
        return productRepository.save(updated);
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
    public PageResult<Product> searchProducts(String name,
                                              String category,
                                              BigDecimal minPrice,
                                              BigDecimal maxPrice,
                                              PageRequest pageRequest) {
        return productRepository.search(name, category, minPrice, maxPrice, pageRequest);
    }
}
