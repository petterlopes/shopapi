package com.peritumct.shopapi.service.usecase;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peritumct.shopapi.dto.OrderDetailDTO;
import com.peritumct.shopapi.dto.OrderStatusUpdateRequest;
import com.peritumct.shopapi.dto.OrderSummaryDTO;
import com.peritumct.shopapi.dto.UpdateOrderRequest;
import com.peritumct.shopapi.model.OrderStatus;

public interface OrderUseCase {

    OrderDetailDTO loadOrderDetail(Long id);

    Page<OrderSummaryDTO> searchOrders(OrderStatus status,
                                       LocalDate from,
                                       LocalDate to,
                                       BigDecimal minTotal,
                                       BigDecimal maxTotal,
                                       Pageable pageable);

    void updateOrder(Long id, UpdateOrderRequest request);

    void updateStatus(Long id, OrderStatusUpdateRequest request);

    void delete(Long id);
}
