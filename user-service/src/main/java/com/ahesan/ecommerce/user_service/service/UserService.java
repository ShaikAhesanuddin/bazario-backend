package com.ahesan.ecommerce.user_service.service;

import com.ahesan.ecommerce.user_service.dto.CreateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UpdateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UserResponse;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUser(UUID userId);

    UserResponse updateUser(UUID userId, UpdateUserRequest request);

    void deleteUser(UUID userId);
}
