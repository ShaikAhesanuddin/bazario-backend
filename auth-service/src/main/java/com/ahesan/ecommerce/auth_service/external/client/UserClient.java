package com.ahesan.ecommerce.auth_service.external.client;

import com.ahesan.ecommerce.auth_service.config.FeignConfig;
import com.ahesan.ecommerce.auth_service.external.dto.CreateUserRequest;
import com.ahesan.ecommerce.auth_service.external.dto.UserResponse;
import com.ahesan.ecommerce.auth_service.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "USER-SERVICE",
        path = "/api/v1/users",
        configuration = FeignConfig.class
)
public interface UserClient {

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody CreateUserRequest request);
}
