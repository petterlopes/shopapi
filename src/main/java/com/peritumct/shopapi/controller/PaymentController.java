package com.peritumct.shopapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peritumct.shopapi.dto.CreatePaymentRequest;
import com.peritumct.shopapi.service.usecase.PaymentUseCase;

@RestController
@RequestMapping("/api/orders/{orderId}/payments")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @PreAuthorize("@securityExpressions.canManageOrder(authentication, #orderId)")
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("orderId") Long orderId,
                                    @RequestBody CreatePaymentRequest req) {
        Long paymentId = paymentUseCase.createPayment(orderId, req);
        return ResponseEntity.ok(paymentId);
    }

    @PreAuthorize("@securityExpressions.hasStaffPrivileges(authentication)")
    @PostMapping("/{paymentId}/approve")
    public ResponseEntity<?> approve(@PathVariable("orderId") Long orderId,
                                     @PathVariable("paymentId") Long paymentId) {
        paymentUseCase.approvePayment(orderId, paymentId);
        return ResponseEntity.ok().build();
    }
}
