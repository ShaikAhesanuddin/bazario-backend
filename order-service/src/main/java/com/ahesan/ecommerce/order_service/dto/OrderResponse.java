package com.ahesan.ecommerce.order_service.dto;


import com.ahesan.ecommerce.order_service.enums.OrderStatus;
import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        UUID orderId,
        Integer quantity,
        BigDecimal price,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        InventoryResponse product,
        AddressResponse address,
        LocalDateTime createdAt
) {
}
