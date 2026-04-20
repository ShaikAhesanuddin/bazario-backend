package com.ahesan.ecommerce.user_service.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String phone
) {
}
