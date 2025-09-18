package com.peritumct.shopapi.infrastructure.persistence.adapter;

import com.peritumct.shopapi.domain.payment.Payment;
import com.peritumct.shopapi.domain.payment.port.PaymentRepository;
import com.peritumct.shopapi.infrastructure.persistence.entity.OrderEntity;
import com.peritumct.shopapi.infrastructure.persistence.entity.PaymentEntity;
import com.peritumct.shopapi.infrastructure.persistence.mapper.PaymentEntityMapper;
import com.peritumct.shopapi.infrastructure.persistence.repository.OrderJpaRepository;
import com.peritumct.shopapi.infrastructure.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final PaymentEntityMapper mapper;

    public JpaPaymentRepositoryAdapter(PaymentJpaRepository paymentJpaRepository,
                                       OrderJpaRepository orderJpaRepository,
                                       PaymentEntityMapper mapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.orderJpaRepository = orderJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        OrderEntity orderEntity = orderJpaRepository.findById(payment.getOrderId())
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + payment.getOrderId()));
        PaymentEntity entity = payment.getId() != null
            ? paymentJpaRepository.findById(payment.getId()).orElse(new PaymentEntity())
            : new PaymentEntity();
        mapper.apply(payment, entity, orderEntity);
        PaymentEntity saved = paymentJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id)
            .map(mapper::toDomain);
    }
}
