package com.guarani.shopapi.controller;

import com.guarani.shopapi.dto.*;
import com.guarani.shopapi.model.*;
import com.guarani.shopapi.repository.IOrderRepository;
import com.guarani.shopapi.repository.IProductRepository;
import com.guarani.shopapi.repository.IUserRepository;
import com.guarani.shopapi.service.ServicoDeCalculoDePreco;
import com.guarani.shopapi.service.spec.OrderSpecifications;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderRepository orderRepo;
    private final IProductRepository productRepo;
    private final IUserRepository userRepo;
    private final ServicoDeCalculoDePreco calculator;

    public OrderController(IOrderRepository orderRepo, IProductRepository productRepo, IUserRepository userRepo, ServicoDeCalculoDePreco calculator) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.calculator = calculator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        return orderRepo.findByIdWithAll(id).map(o -> {
            List<OrderItemDTO> items = o.getItems().stream().map(it ->
                new OrderItemDTO(
                    it.getProduct().getId(),
                    it.getProduct().getName(),
                    it.getUnitPrice(),
                    it.getQuantity(),
                    it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()))
                )
            ).toList();
            OrderDetailDTO dto = new OrderDetailDTO(
                o.getId(), o.getCreatedAt(), o.getStatus(),
                o.getSubtotal(), o.getDiscount(), o.getShippingFee(),
                o.getTotal(), o.getUser().getUsername(), items
            );
            return ResponseEntity.ok(dto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<OrderSummaryDTO> list(@RequestParam(value="status", required = false) OrderStatus status,
                                      @RequestParam(value="from", required = false) LocalDate from,
                                      @RequestParam(value="to", required = false) LocalDate to,
                                      @RequestParam(value="minTotal", required = false) BigDecimal minTotal,
                                      @RequestParam(value="maxTotal", required = false) BigDecimal maxTotal,
                                      Pageable pageable) {
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.atTime(LocalTime.MAX) : null;
        Specification<Order> spec = OrderSpecifications.filter(status, fromDt, toDt, minTotal, maxTotal);
        Page<Order> page = orderRepo.findAll(spec, pageable);
        return page.map(o -> new OrderSummaryDTO(
            o.getId(), o.getCreatedAt(), o.getStatus(), o.getTotal()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @Valid @RequestBody UpdateOrderRequest req,
                                    Authentication auth) {
        Optional<Order> orderOpt = orderRepo.findByIdWithAll(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOpt.get();

        String username = auth.getName();
        boolean isAdminOrOperator = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN") || a.getAuthority().contains("OPERATOR"));

        if (!order.getUser().getUsername().equals(username) && !isAdminOrOperator) {
            return ResponseEntity.status(403).build();
        }

        order.getItems().clear();
        if (req.getItems() != null) {
            for (UpdateOrderRequest.Item itemReq : req.getItems()) {
                Product product = productRepo.findById(itemReq.getProductId()).orElse(null);
                if (product == null) {
                    return ResponseEntity.badRequest().body("Produto não encontrado: " + itemReq.getProductId());
                }
                OrderItem item = new OrderItem(order, product, itemReq.getQuantity(), product.getPrice());
                order.getItems().add(item);
            }
        }

        if (req.getDiscount() != null) {
            order.setDiscount(req.getDiscount());
        }
        if (req.getShippingFee() != null) {
            order.setShippingFee(req.getShippingFee());
        }

        calculator.recalc(order);
        orderRepo.save(order);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable("id") Long id, @RequestBody OrderStatusUpdateRequest request) {
        Optional<Order> orderOpt = orderRepo.findById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Order order = orderOpt.get();
        order.setStatus(request.getStatus());
        calculator.recalc(order);
        orderRepo.save(order);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
