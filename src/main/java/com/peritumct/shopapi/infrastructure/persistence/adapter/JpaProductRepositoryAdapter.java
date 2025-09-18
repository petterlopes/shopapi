package com.peritumct.shopapi.infrastructure.persistence.adapter;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.product.port.ProductRepository;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.infrastructure.persistence.entity.ProductEntity;
import com.peritumct.shopapi.infrastructure.persistence.mapper.ProductEntityMapper;
import com.peritumct.shopapi.infrastructure.persistence.repository.ProductJpaRepository;
import com.peritumct.shopapi.service.spec.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductEntityMapper mapper;

    public JpaProductRepositoryAdapter(ProductJpaRepository productJpaRepository,
                                       ProductEntityMapper mapper) {
        this.productJpaRepository = productJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity saved = productJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }

    @Override
    public PageResult<Product> search(String name, String category, BigDecimal minPrice, BigDecimal maxPrice, PageRequest pageRequest) {
        org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size());
        Page<ProductEntity> page = productJpaRepository.findAll(ProductSpecifications.filter(name, category, minPrice, maxPrice), pageable);
        List<Product> items = page.getContent().stream()
            .map(mapper::toDomain)
            .toList();
        return new PageResult<>(items, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }
}
