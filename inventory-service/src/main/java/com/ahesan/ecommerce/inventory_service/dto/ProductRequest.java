package com.ahesan.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        String name,
        @NotBlank(message = "Product SKU code is required")
        String skuCode,
        @NotNull
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        @NotNull
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,
        Boolean active,
        String description,
        @DecimalMin(value = "0.0", message = "Discount must be >= 0")
        @DecimalMax(value = "100.0", message = "Discount must be <= 100")
        BigDecimal discountPercentage,
        @NotBlank(message = "Brand is required")
        String brand,

        @NotBlank(message = "Category is required")
        String category,

        String imageUrl
) {
}
