package com.peritumct.shopapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peritumct.shopapi.dto.CreatePaymentRequest;
import com.peritumct.shopapi.model.Order;
import com.peritumct.shopapi.model.OrderStatus;
import com.peritumct.shopapi.model.Payment;
import com.peritumct.shopapi.model.PaymentStatus;
import com.peritumct.shopapi.repository.IOrderRepository;
import com.peritumct.shopapi.repository.IPaymentRepository;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.usecase.PaymentUseCase;

@Service
public class PaymentApplicationService implements PaymentUseCase {

    private final IOrderRepository orderRepository;
    private final IPaymentRepository paymentRepository;

    public PaymentApplicationService(IOrderRepository orderRepository,
                                     IPaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Long createPayment(Long orderId, CreatePaymentRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + orderId));
        Payment payment = new Payment(order, request.getMethod(), order.getTotal());
        paymentRepository.save(payment);
        return payment.getId();
    }

    @Override
    @Transactional
    public void approvePayment(Long orderId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento nao encontrado: " + paymentId));
        if (!payment.getOrderRef().getId().equals(orderId)) {
            throw new IllegalArgumentException("Pagamento nao pertence ao pedido informado");
        }
        payment.setStatus(PaymentStatus.APPROVED);
        paymentRepository.save(payment);

        Order order = payment.getOrderRef();
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }
}
