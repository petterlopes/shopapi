package com.peritumct.shopapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peritumct.shopapi.dto.OrderDetailDTO;
import com.peritumct.shopapi.dto.OrderItemDTO;
import com.peritumct.shopapi.dto.OrderStatusUpdateRequest;
import com.peritumct.shopapi.dto.OrderSummaryDTO;
import com.peritumct.shopapi.dto.UpdateOrderRequest;
import com.peritumct.shopapi.model.Order;
import com.peritumct.shopapi.model.OrderItem;
import com.peritumct.shopapi.model.OrderStatus;
import com.peritumct.shopapi.model.Product;
import com.peritumct.shopapi.repository.IOrderRepository;
import com.peritumct.shopapi.repository.IProductRepository;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.spec.OrderSpecifications;
import com.peritumct.shopapi.service.usecase.OrderUseCase;

@Service
public class OrderApplicationService implements OrderUseCase {

    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;
    private final ServicoDeCalculoDePreco calculator;

    public OrderApplicationService(IOrderRepository orderRepository,
                                   IProductRepository productRepository,
                                   ServicoDeCalculoDePreco calculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.calculator = calculator;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailDTO loadOrderDetail(Long id) {
        Order order = orderRepository.findByIdWithAll(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
        return new OrderDetailDTO(
            order.getId(),
            order.getCreatedAt(),
            order.getStatus(),
            order.getSubtotal(),
            order.getDiscount(),
            order.getShippingFee(),
            order.getTotal(),
            order.getUser().getUsername(),
            toOrderItemDtos(order.getItems())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryDTO> searchOrders(OrderStatus status,
                                              LocalDate from,
                                              LocalDate to,
                                              BigDecimal minTotal,
                                              BigDecimal maxTotal,
                                              Pageable pageable) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(LocalTime.MAX) : null;
        Specification<Order> spec = OrderSpecifications.filter(status, fromDt, toDt, minTotal, maxTotal);
        return orderRepository.findAll(spec, pageable)
            .map(order -> new OrderSummaryDTO(order.getId(), order.getCreatedAt(), order.getStatus(), order.getTotal()));
    }

    @Override
    @Transactional
    public void updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderRepository.findByIdWithAll(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));

        rebuildOrderItems(order, request);

        if (request.getDiscount() != null) {
            order.setDiscount(request.getDiscount());
        }
        if (request.getShippingFee() != null) {
            order.setShippingFee(request.getShippingFee());
        }

        calculator.recalc(order);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
        order.setStatus(request.getStatus());
        calculator.recalc(order);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido nao encontrado: " + id);
        }
        orderRepository.deleteById(id);
    }

    private List<OrderItemDTO> toOrderItemDtos(List<OrderItem> items) {
        return items.stream()
            .map(item -> new OrderItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
            .toList();
    }

    private void rebuildOrderItems(Order order, UpdateOrderRequest request) {
        order.getItems().clear();
        if (request.getItems() == null) {
            return;
        }

        for (UpdateOrderRequest.Item itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado: " + itemReq.getProductId()));
            OrderItem item = new OrderItem(order, product, itemReq.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }
    }
}
