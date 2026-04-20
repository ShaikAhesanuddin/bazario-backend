package com.ahesan.ecommerce.auth_service.external.dto;

import java.util.UUID;

public record CreateUserRequest(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone
) {
}
