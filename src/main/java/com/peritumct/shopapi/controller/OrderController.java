package com.peritumct.shopapi.controller;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.OrderItem;
import com.peritumct.shopapi.domain.order.OrderSearchFilters;
import com.peritumct.shopapi.domain.order.OrderStatus;
import com.peritumct.shopapi.domain.order.OrderUpdateCommand;
import com.peritumct.shopapi.domain.order.OrderUpdateItem;
import com.peritumct.shopapi.domain.shared.PageRequest;
import com.peritumct.shopapi.domain.shared.PageResult;
import com.peritumct.shopapi.dto.OrderDetailDTO;
import com.peritumct.shopapi.dto.OrderItemDTO;
import com.peritumct.shopapi.dto.OrderStatusUpdateRequest;
import com.peritumct.shopapi.dto.OrderSummaryDTO;
import com.peritumct.shopapi.dto.UpdateOrderRequest;
import com.peritumct.shopapi.service.usecase.OrderUseCase;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> get(@PathVariable("id") Long id) {
        Order order = orderUseCase.loadOrder(id);
        return ResponseEntity.ok(toDetailDto(order));
    }

    @GetMapping
    public Page<OrderSummaryDTO> list(@RequestParam(value = "status", required = false) OrderStatus status,
                                      @RequestParam(value = "from", required = false) LocalDate from,
                                      @RequestParam(value = "to", required = false) LocalDate to,
                                      @RequestParam(value = "minTotal", required = false) BigDecimal minTotal,
                                      @RequestParam(value = "maxTotal", required = false) BigDecimal maxTotal,
                                      Pageable pageable) {
        OrderSearchFilters filters = new OrderSearchFilters(status, from, to, minTotal, maxTotal);
        PageResult<Order> result = orderUseCase.searchOrders(filters, new PageRequest(pageable.getPageNumber(), pageable.getPageSize()));
        List<OrderSummaryDTO> summaries = result.items().stream()
            .map(this::toSummaryDto)
            .toList();
        return new PageImpl<>(summaries, pageable, result.totalElements());
    }

    @PreAuthorize("@securityExpressions.canManageOrder(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody UpdateOrderRequest req) {
        orderUseCase.updateOrder(id, toUpdateCommand(req));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id,
                                             @RequestBody OrderStatusUpdateRequest request) {
        orderUseCase.updateStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@securityExpressions.isAdmin(authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        orderUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private OrderDetailDTO toDetailDto(Order order) {
        List<OrderItemDTO> items = order.getItems().stream()
            .map(this::toItemDto)
            .toList();
        return new OrderDetailDTO(
            order.getId(),
            order.getCreatedAt(),
            order.getStatus(),
            order.getSubtotal(),
            order.getDiscount(),
            order.getShippingFee(),
            order.getTotal(),
            order.getUser().getUsername(),
            items
        );
    }

    private OrderSummaryDTO toSummaryDto(Order order) {
        return new OrderSummaryDTO(order.getId(), order.getCreatedAt(), order.getStatus(), order.getTotal());
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        return new OrderItemDTO(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getUnitPrice(),
            item.getQuantity(),
            item.getLineTotal()
        );
    }

    private OrderUpdateCommand toUpdateCommand(UpdateOrderRequest request) {
        List<OrderUpdateItem> items = request.getItems() == null ? null : request.getItems().stream()
            .map(it -> new OrderUpdateItem(it.getProductId(), it.getQuantity()))
            .toList();
        return new OrderUpdateCommand(items, request.getDiscount(), request.getShippingFee());
    }
}
