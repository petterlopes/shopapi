package com.peritumct.shopapi.domain.payment.port;

import com.peritumct.shopapi.domain.payment.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
}
