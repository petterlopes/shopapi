package com.peritumct.shopapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
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

import com.peritumct.shopapi.dto.OrderStatusUpdateRequest;
import com.peritumct.shopapi.dto.OrderSummaryDTO;
import com.peritumct.shopapi.dto.UpdateOrderRequest;
import com.peritumct.shopapi.model.OrderStatus;
import com.peritumct.shopapi.service.usecase.OrderUseCase;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderUseCase.loadOrderDetail(id));
    }

    @GetMapping
    public Page<OrderSummaryDTO> list(@RequestParam(value = "status", required = false) OrderStatus status,
                                      @RequestParam(value = "from", required = false) LocalDate from,
                                      @RequestParam(value = "to", required = false) LocalDate to,
                                      @RequestParam(value = "minTotal", required = false) BigDecimal minTotal,
                                      @RequestParam(value = "maxTotal", required = false) BigDecimal maxTotal,
                                      Pageable pageable) {
        return orderUseCase.searchOrders(status, from, to, minTotal, maxTotal, pageable);
    }

    @PreAuthorize("@securityExpressions.canManageOrder(authentication, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @Valid @RequestBody UpdateOrderRequest req) {
        orderUseCase.updateOrder(id, req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id,
                                             @RequestBody OrderStatusUpdateRequest request) {
        orderUseCase.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@securityExpressions.isAdmin(authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        orderUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
