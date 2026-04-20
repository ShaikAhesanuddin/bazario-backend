package com.ahesan.ecommerce.order_service.external.dto;

import com.ahesan.ecommerce.order_service.external.enums.AddressType;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String city,
        String state,
        String pincode,
        String landmark,
        String phone
) {
}
