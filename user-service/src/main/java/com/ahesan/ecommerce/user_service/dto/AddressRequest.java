package com.ahesan.ecommerce.user_service.dto;

import com.ahesan.ecommerce.user_service.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        String street,
        @NotBlank(message = "City is required")
        String city,
        String state,
        String country,
        @NotBlank(message = "Pincode is required")
        @Pattern(regexp = "\\d{6}", message = "Invalid pincode")
        String pincode,
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
        @NotBlank(message = "Phone number is required")
        String phone,
        @NotBlank(message = "Landmark is required")
        String landmark,
        @NotNull(message = "Address type is required")
        AddressType addressType,
        boolean isDefault
) {
}
