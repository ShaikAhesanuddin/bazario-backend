package com.ahesan.ecommerce.order_service.external.dto;

public record UpdateStockRequest(
        String productId,
        Integer quantity
) {
}
