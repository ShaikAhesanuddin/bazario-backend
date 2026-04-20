package com.ahesan.ecommerce.order_service.service;

import com.ahesan.ecommerce.order_service.dto.OrderRequest;
import com.ahesan.ecommerce.order_service.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, UUID userId);

    List<OrderResponse> getAllOrders(UUID userId);
}
