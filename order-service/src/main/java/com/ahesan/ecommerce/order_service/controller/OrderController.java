package com.ahesan.ecommerce.order_service.controller;


import com.ahesan.ecommerce.order_service.dto.OrderRequest;
import com.ahesan.ecommerce.order_service.dto.OrderResponse;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import com.ahesan.ecommerce.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "/api/v1/orders",
        produces = {
                "application/json",
                "application/xml"
        }
)
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ) {

        log.info("Received order request | Product: {} | Quantity: {}",
                request.productId(), request.quantity());

        OrderResponse response = orderService.placeOrder(request, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Order placed successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestHeader("X-User-Id") UUID userId
    ) {

        List<OrderResponse> orders = orderService.getAllOrders(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Orders fetched successfully", orders)
        );
    }
}
