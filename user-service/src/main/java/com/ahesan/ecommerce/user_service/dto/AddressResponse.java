package com.ahesan.ecommerce.user_service.dto;

import com.ahesan.ecommerce.user_service.enums.AddressType;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String street,
        String city,
        String state,
        String country,
        String pincode,
        String landmark,
        String phone,
        AddressType addressType,
        boolean isDefault
) {
}
