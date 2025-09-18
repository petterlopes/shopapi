package com.peritumct.shopapi.infrastructure.persistence.mapper;

import com.peritumct.shopapi.domain.payment.Payment;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityMapper {

    public Payment toDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Payment(entity.getId(),
            entity.getOrder().getId(),
            entity.getMethod(),
            entity.getStatus(),
            entity.getAmount(),
            entity.getCreatedAt());
    }

    public void apply(Payment domain, PaymentEntity entity, OrderEntity orderEntity) {
        entity.setId(domain.getId());
        entity.setOrder(orderEntity);
        entity.setMethod(domain.getMethod());
        entity.setStatus(domain.getStatus());
        entity.setAmount(domain.getAmount());
        entity.setCreatedAt(domain.getCreatedAt());
    }
}
