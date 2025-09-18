package com.peritumct.shopapi.service;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderStatus;
import com.peritumct.shopapi.domain.order.port.OrderRepository;
import com.peritumct.shopapi.domain.payment.CreatePaymentCommand;
import com.peritumct.shopapi.domain.payment.Payment;
import com.peritumct.shopapi.domain.payment.PaymentStatus;
import com.peritumct.shopapi.domain.payment.port.PaymentRepository;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.usecase.PaymentUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentApplicationService implements PaymentUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public PaymentApplicationService(OrderRepository orderRepository,
                                     PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public Long createPayment(Long orderId, CreatePaymentCommand command) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + orderId));
        Payment payment = new Payment(null, order.getId(), command.method(), PaymentStatus.PENDING, order.getTotal(), LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);
        return saved.getId();
    }

    @Override
    @Transactional
    public void approvePayment(Long orderId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento nao encontrado: " + paymentId));
        if (!payment.getOrderId().equals(orderId)) {
            throw new IllegalArgumentException("Pagamento nao pertence ao pedido informado");
        }
        Payment approved = payment.withStatus(PaymentStatus.APPROVED);
        paymentRepository.save(approved);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + orderId));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }
}
