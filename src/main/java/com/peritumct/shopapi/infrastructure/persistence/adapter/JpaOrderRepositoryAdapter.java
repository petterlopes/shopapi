package com.peritumct.shopapi.infrastructure.persistence.adapter;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.domain.order.port.OrderRepository;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.ProductEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.UserEntity;
import com.peritumct.shopapi.infrastructure.persistence.mapper.OrderEntityMapper;
import com.peritumct.shopapi.infrastructure.persistence.repository.OrderJpaRepository;
import com.peritumct.shopapi.infrastructure.persistence.repository.ProductJpaRepository;
import com.peritumct.shopapi.infrastructure.persistence.repository.UserJpaRepository;
import com.peritumct.shopapi.service.spec.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderEntityMapper orderEntityMapper;

    public JpaOrderRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                     UserJpaRepository userJpaRepository,
                                     ProductJpaRepository productJpaRepository,
                                     OrderEntityMapper orderEntityMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id)
            .map(orderEntityMapper::toDomain);
    }

    @Override
    public Optional<Order> findDetailedById(Long id) {
        return orderJpaRepository.fetchDetailedById(id)
            .map(orderEntityMapper::toDomain);
    }

    @Override
    public Optional<Order> findWithUser(Long id) {
        return orderJpaRepository.fetchWithUser(id)
            .map(orderEntityMapper::toDomain);
    }

    @Override
    public PageResult<Order> search(OrderSearchFilters filters, PageRequest pageRequest) {
        org.springframework.data.domain.PageRequest pageable = org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size());
        Page<OrderEntity> page = orderJpaRepository.findAll(OrderSpecifications.filter(filters), pageable);
        List<Order> content = page.getContent().stream()
            .map(orderEntityMapper::toDomain)
            .toList();
        return new PageResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }

    @Override
    public Order save(Order order) {
        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new IllegalArgumentException("Order user must be provided");
        }

        UserEntity userEntity = userJpaRepository.findById(order.getUser().getId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + order.getUser().getId()));

        Set<Long> productIds = order.getItems().stream()
            .map(item -> item.getProduct().getId())
            .collect(Collectors.toSet());

        List<ProductEntity> products = productJpaRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("One or more products not found: " + productIds);
        }

        OrderEntity entity = order.getId() != null
            ? orderJpaRepository.findById(order.getId()).orElse(new OrderEntity())
            : new OrderEntity();

        orderEntityMapper.apply(order, entity, userEntity, products);
        OrderEntity saved = orderJpaRepository.save(entity);
        return orderEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(Long id) {
        return orderJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        orderJpaRepository.deleteById(id);
    }
}
