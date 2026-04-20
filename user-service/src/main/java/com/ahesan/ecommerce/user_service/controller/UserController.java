package com.ahesan.ecommerce.user_service.controller;

import com.ahesan.ecommerce.user_service.dto.CreateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UpdateUserRequest;
import com.ahesan.ecommerce.user_service.dto.UserResponse;
import com.ahesan.ecommerce.user_service.response.ApiResponse;
import com.ahesan.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(
        value = "/api/v1/users",
        produces = {
                "application/json",
                "application/xml"
        }
)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid
            @RequestBody CreateUserRequest request
    ) {
        ApiResponse<UserResponse> response = ApiResponse.success("User created successfully", userService.createUser(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        userService.getUser(userId)
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid
            @RequestBody UpdateUserRequest request
    ) {
        ApiResponse<UserResponse> response = ApiResponse.success("User updated successfully", userService.updateUser(userId, request));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        userService.deleteUser(userId);
        ApiResponse<Void> response = ApiResponse.success("Your account will be deleted within 24 hours.", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
