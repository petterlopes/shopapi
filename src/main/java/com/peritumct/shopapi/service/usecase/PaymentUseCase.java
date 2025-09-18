package com.peritumct.shopapi.service.usecase;

import com.peritumct.shopapi.dto.CreatePaymentRequest;

public interface PaymentUseCase {

    Long createPayment(Long orderId, CreatePaymentRequest request);

    void approvePayment(Long orderId, Long paymentId);
}
