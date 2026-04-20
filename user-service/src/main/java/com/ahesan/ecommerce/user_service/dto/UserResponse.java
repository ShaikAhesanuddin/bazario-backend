package com.ahesan.ecommerce.user_service.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String status
) {
}
