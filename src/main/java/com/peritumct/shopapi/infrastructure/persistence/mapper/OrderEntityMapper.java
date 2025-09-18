package com.peritumct.shopapi.infrastructure.persistence.mapper;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderItem;
import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderItemEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.ProductEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {

    private final UserEntityMapper userMapper;
    private final ProductEntityMapper productMapper;

    public OrderEntityMapper(UserEntityMapper userMapper, ProductEntityMapper productMapper) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        List<OrderItem> items = entity.getItems().stream()
            .map(this::toDomainItem)
            .toList();
        return new Order(
            entity.getId(),
            userMapper.toDomain(entity.getUser()),
            entity.getCreatedAt(),
            entity.getStatus(),
            items,
            entity.getSubtotal(),
            entity.getDiscount(),
            entity.getShippingFee(),
            entity.getTotal());
    }

    private OrderItem toDomainItem(OrderItemEntity entity) {
        Product product = productMapper.toDomain(entity.getProduct());
        return new OrderItem(product, entity.getQuantity(), entity.getUnitPrice());
    }

    public OrderEntity toEntity(Order domain, UserEntity userEntity, List<ProductEntity> productEntities) {
        OrderEntity entity = new OrderEntity();
        apply(domain, entity, userEntity, productEntities);
        return entity;
    }

    public void apply(Order domain, OrderEntity target, UserEntity userEntity, List<ProductEntity> productEntities) {
        target.setId(domain.getId());
        target.setUser(userEntity);
        target.setCreatedAt(domain.getCreatedAt());
        target.setStatus(domain.getStatus());
        target.setSubtotal(domain.getSubtotal());
        target.setDiscount(domain.getDiscount());
        target.setShippingFee(domain.getShippingFee());
        target.setTotal(domain.getTotal());

        Map<Long, ProductEntity> productById = productEntities.stream()
            .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        for (OrderItem item : domain.getItems()) {
            ProductEntity productEntity = productById.get(item.getProduct().getId());
            if (productEntity == null) {
                throw new IllegalArgumentException("Product entity not loaded for id: " + item.getProduct().getId());
            }
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrder(target);
            itemEntity.setProduct(productEntity);
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            itemEntities.add(itemEntity);
        }

        target.getItems().clear();
        target.getItems().addAll(itemEntities);
    }
}
