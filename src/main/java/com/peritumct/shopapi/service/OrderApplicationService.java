package com.peritumct.shopapi.service;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.domain.order.OrderStatus;
import com.peritumct.shopapi.domain.order.OrderUpdateCommand;
import com.peritumct.shopapi.domain.order.OrderUpdateItem;
import com.peritumct.shopapi.domain.order.port.OrderRepository;
import com.peritumct.shopapi.domain.order.service.PriceCalculator;
import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.product.port.ProductRepository;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.service.exception.ResourceNotFoundException;
import com.peritumct.shopapi.service.usecase.OrderUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderApplicationService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PriceCalculator calculator;

    public OrderApplicationService(OrderRepository orderRepository,
                                   ProductRepository productRepository,
                                   PriceCalculator calculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.calculator = calculator;
    }

    @Override
    @Transactional(readOnly = true)
    public Order loadOrder(Long id) {
        return orderRepository.findDetailedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Order> searchOrders(OrderSearchFilters filters, PageRequest pageRequest) {
        return orderRepository.search(filters, pageRequest);
    }

    @Override
    @Transactional
    public void updateOrder(Long id, OrderUpdateCommand command) {
        Order order = orderRepository.findDetailedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));

        rebuildOrderItems(order, command.items());

        if (command.discount() != null) {
            order.setDiscount(command.discount());
        }
        if (command.shippingFee() != null) {
            order.setShippingFee(command.shippingFee());
        }

        calculator.recalculate(order);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
        order.setStatus(status);
        calculator.recalculate(order);
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

    private void rebuildOrderItems(Order order, List<OrderUpdateItem> items) {
        order.clearItems();
        if (items == null) {
            return;
        }

        for (OrderUpdateItem item : items) {
            Product product = productRepository.findById(item.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado: " + item.productId()));
            order.addItem(product, item.quantity(), product.getPrice());
        }
    }
}
