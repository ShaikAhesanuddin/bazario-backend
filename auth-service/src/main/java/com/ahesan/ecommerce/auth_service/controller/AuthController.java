package com.ahesan.ecommerce.auth_service.controller;

import com.ahesan.ecommerce.auth_service.dto.AuthResponse;
import com.ahesan.ecommerce.auth_service.dto.LoginRequest;
import com.ahesan.ecommerce.auth_service.dto.RegisterRequest;
import com.ahesan.ecommerce.auth_service.response.ApiResponse;
import com.ahesan.ecommerce.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/v1/auth",
        produces = {
                "application/json",
                "application/xml"
        }

)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login successful",
                        authService.login(request))
        );
    }

}
