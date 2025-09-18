package com.peritumct.shopapi.security;

import com.peritumct.shopapi.domain.order.Order;
import com.peritumct.shopapi.domain.order.port.OrderRepository;
import com.peritumct.shopapi.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityExpressions")
public class SecurityExpressions {

    private final OrderRepository orderRepository;

    public SecurityExpressions(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean hasRole(Authentication authentication, Role role) {
        return SecurityUtils.hasRole(authentication, role);
    }

    public boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, Role.ADMIN);
    }

    public boolean hasStaffPrivileges(Authentication authentication) {
        return SecurityUtils.hasAnyRole(authentication, Role.ADMIN, Role.OPERATOR);
    }

    public boolean canManageOrder(Authentication authentication, Long orderId) {
        if (orderId == null) {
            return false;
        }
        return orderRepository.findWithUser(orderId)
            .map(order -> canManageOrder(authentication, order))
            .orElse(true);
    }

    public boolean canManageOrder(Authentication authentication, Order order) {
        if (order == null) {
            return false;
        }
        return SecurityUtils.isSelfOrHasAnyRole(authentication, order.getUser().getUsername(), Role.ADMIN, Role.OPERATOR);
    }
}
