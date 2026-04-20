package com.ahesan.ecommerce.order_service.external.client;

import com.ahesan.ecommerce.order_service.external.config.UserFeignConfig;
import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "USER-SERVICE",
        path = "/api/v1/users/me/addresses",
        configuration = UserFeignConfig.class
)
public interface UserClient {

    @GetMapping("/{addressId}")
    ApiResponse<AddressResponse> getAddressById(
            @PathVariable UUID addressId,
            @RequestHeader("X-User-Id") String userId
    );
}
