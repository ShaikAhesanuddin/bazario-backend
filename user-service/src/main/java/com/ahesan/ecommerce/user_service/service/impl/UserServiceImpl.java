package com.ahesan.ecommerce.user_service.service.impl;

import com.ahesan.ecommerce.user_service.dto.CreateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UpdateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UserResponse;
import com.ahesan.ecommerce.user_service.entity.User;
import com.ahesan.ecommerce.user_service.enums.UserStatus;
import com.ahesan.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.ahesan.ecommerce.user_service.exception.UserNotFoundException;
import com.ahesan.ecommerce.user_service.mapper.UserMapper;
import com.ahesan.ecommerce.user_service.repository.UserRepository;
import com.ahesan.ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        log.info("Create user request received for email={}", request.email());

        if (userRepo.existsByEmail(request.email())) {
            log.warn("User creation failed: email already exists, email={}", request.email());
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + request.email()
            );
        }

        User user = userMapper.toEntity(request);

        log.debug("Mapped CreateUserRequest to User entity for email={}", request.email());

        User savedUser = userRepo.save(user);

        log.info("User successfully created with id={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUser(UUID userId) {

        log.info("Get user request received for userId={}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for userId={}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        log.debug("User entity fetched from DB for userId={}", userId);

        UserResponse response = userMapper.toResponse(user);

        log.info("User successfully retrieved for userId={}", userId);

        return response;
    }

    @Override
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {

        log.info("Update user request received for userId={}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Update failed: user not found for userId={}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        boolean isUpdated = false;

        if (request.firstName() != null && !request.firstName().isBlank()) {
            log.debug("Updating firstName for userId={}", userId);
            user.setFirstName(request.firstName());
            isUpdated = true;
        }

        if (request.lastName() != null && !request.lastName().isBlank()) {
            log.debug("Updating lastName for userId={}", userId);
            user.setLastName(request.lastName());
            isUpdated = true;
        }

        if (request.phone() != null && !request.phone().isBlank()) {
            log.debug("Updating phone for userId={}", userId);
            user.setPhone(request.phone());
            isUpdated = true;
        }

        if (!isUpdated) {
            log.warn("No valid fields provided for update for userId={}", userId);
        }

        User savedUser = userRepo.save(user);

        log.info("User successfully updated for userId={}", userId);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public void deleteUser(UUID userId) {

        log.info("Delete user request received for userId={}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Delete failed: user not found for userId={}", userId);
                    return new UserNotFoundException("User not found with id: " + userId);
                });

        if (user.getStatus() == UserStatus.DELETED) {
            log.warn("User already deleted for userId={}", userId);
            return;
        }

        log.debug("Soft deleting user for userId={}, currentStatus={}", userId, user.getStatus());

        user.setStatus(UserStatus.DELETED);

        userRepo.save(user);

        log.info("User successfully soft deleted for userId={}", userId);
    }
}
