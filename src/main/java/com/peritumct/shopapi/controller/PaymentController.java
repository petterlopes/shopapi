package com.peritumct.shopapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.peritumct.shopapi.dto.CreatePaymentRequest;
import com.peritumct.shopapi.model.*;
import com.peritumct.shopapi.repository.IOrderRepository;
import com.peritumct.shopapi.repository.IPaymentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders/{orderId}/payments")
public class PaymentController {

    private final IOrderRepository orderRepo;
    private final IPaymentRepository paymentRepo;

    public PaymentController(IOrderRepository orderRepo, IPaymentRepository paymentRepo) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
    }

    @PreAuthorize("@securityExpressions.canManageOrder(authentication, #orderId)")
    @PostMapping
    public ResponseEntity<?> create(@PathVariable("orderId") Long orderId,
                                    @RequestBody CreatePaymentRequest req) {
        Optional<Order> opt = orderRepo.findById(orderId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Order o = opt.get();

        Payment p = new Payment(o, req.getMethod(), o.getTotal());
        paymentRepo.save(p);
        return ResponseEntity.ok(p.getId());
    }

    @PreAuthorize("@securityExpressions.hasStaffPrivileges(authentication)")
    @PostMapping("/{paymentId}/approve")
    public ResponseEntity<?> approve(@PathVariable("orderId") Long orderId,
                                     @PathVariable("paymentId") Long paymentId) {
        Optional<Payment> opt = paymentRepo.findById(paymentId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Payment p = opt.get();
        if (!p.getOrderRef().getId().equals(orderId)) return ResponseEntity.badRequest().body("Pagamento não pertence ao pedido");
        p.setStatus(PaymentStatus.APPROVED);
        paymentRepo.save(p);
        Order o = p.getOrderRef();
        o.setStatus(OrderStatus.PAID);
        orderRepo.save(o);
        return ResponseEntity.ok().build();
    }
}




