package com.ahesan.ecommerce.order_service.external.dto;

import java.math.BigDecimal;

public record InventoryResponse(
        String id,
        String name,
        String skuCode,
        Integer quantity,
        BigDecimal price,

        Boolean active,
        String description,
        BigDecimal discountPercentage,
        BigDecimal discountedPrice,
        String brand,
        String category,

        String imageUrl,
        boolean inStock

) {
}
