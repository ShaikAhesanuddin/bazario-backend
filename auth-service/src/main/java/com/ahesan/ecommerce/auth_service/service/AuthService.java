package com.ahesan.ecommerce.auth_service.service;

import com.ahesan.ecommerce.auth_service.dto.AuthResponse;
import com.ahesan.ecommerce.auth_service.dto.LoginRequest;
import com.ahesan.ecommerce.auth_service.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
