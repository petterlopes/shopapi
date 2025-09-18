package com.peritumct.shopapi.service.usecase;

import com.peritumct.shopapi.domain.payment.CreatePaymentCommand;

public interface PaymentUseCase {

    Long createPayment(Long orderId, CreatePaymentCommand command);

    void approvePayment(Long orderId, Long paymentId);
}
