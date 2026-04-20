package com.ahesan.ecommerce.auth_service.service.impl;

import com.ahesan.ecommerce.auth_service.dto.AuthResponse;
import com.ahesan.ecommerce.auth_service.dto.LoginRequest;
import com.ahesan.ecommerce.auth_service.dto.RegisterRequest;
import com.ahesan.ecommerce.auth_service.dto.UserInfo;
import com.ahesan.ecommerce.auth_service.entity.AuthUser;
import com.ahesan.ecommerce.auth_service.exception.InvalidCredentialsException;
import com.ahesan.ecommerce.auth_service.exception.UserAlreadyExistsException;
import com.ahesan.ecommerce.auth_service.external.client.UserClient;
import com.ahesan.ecommerce.auth_service.external.dto.CreateUserRequest;
import com.ahesan.ecommerce.auth_service.mapper.AuthUserMapper;
import com.ahesan.ecommerce.auth_service.repository.AuthUserRepository;
import com.ahesan.ecommerce.auth_service.service.AuthService;
import com.ahesan.ecommerce.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;
    private final AuthUserMapper mapper;

    @Override
    public void register(RegisterRequest request) {

        log.info("Register request received for email={}", request.email());

        if (repo.existsByEmail(request.email())) {
            log.warn("Registration failed: email already exists, email={}", request.email());
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        AuthUser authEntity = mapper.toEntity(request);
        authEntity.setPassword(passwordEncoder.encode(request.password()));

        repo.save(authEntity);

        log.info("Auth user created successfully with id={}, email={}",
                authEntity.getId(), authEntity.getEmail());

        CreateUserRequest userRequest = new CreateUserRequest(
                authEntity.getId(),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phone()
        );

        log.info("Calling USER-SERVICE to create profile, userId={}", authEntity.getId());

        userClient.createUser(userRequest);

        log.info("User profile created successfully in USER-SERVICE, userId={}",
                authEntity.getId());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("Login attempt for email={}", request.email());

        AuthUser user = repo.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found, email={}", request.email());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: invalid password, email={}", request.email());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("Login successful for userId={}, email={}",
                user.getId(), user.getEmail());

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpiration();

        log.debug("JWT token generated for userId={}, expiresAt={}",
                user.getId(), expiresAt);

        return new AuthResponse(
                token,
                "Bearer",
                expiresAt,
                new UserInfo(
                        user.getEmail(),
                        user.getRole().name()
                )
        );
    }
}
