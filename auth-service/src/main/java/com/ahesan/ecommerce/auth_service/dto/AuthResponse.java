package com.ahesan.ecommerce.auth_service.dto;

public record AuthResponse(
        String token,
        String type,
        long expiresAt,
        UserInfo user
) {
}
