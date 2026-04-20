package com.ahesan.ecommerce.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        String firstName,
        String lastName,
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
        String phone
) {
}
