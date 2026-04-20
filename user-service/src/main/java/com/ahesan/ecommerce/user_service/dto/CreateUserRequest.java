package com.ahesan.ecommerce.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateUserRequest(
        @NotNull(message = "User ID is required")
        UUID id,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        String firstName,
        String lastName,
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
        String phone
) {
}
