package com.peritumct.shopapi.dto;

import com.peritumct.shopapi.domain.payment.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public class CreatePaymentRequest {
    @NotNull
    private PaymentMethod method;

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
}
