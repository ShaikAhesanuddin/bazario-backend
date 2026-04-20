package com.ahesan.ecommerce.order_service.external.dto;

public record ReduceStockRequest(
        String productId,
        Integer quantity
) {
}
