package com.ahesan.ecommerce.inventory_service.dto;

import java.math.BigDecimal;

public record ProductResponse(
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