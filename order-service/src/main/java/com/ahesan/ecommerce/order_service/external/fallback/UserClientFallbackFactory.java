package com.ahesan.ecommerce.order_service.external.fallback;

import com.ahesan.ecommerce.order_service.external.client.UserClient;
import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {

            @Override
            public ApiResponse<AddressResponse> getAddressById(UUID addressId, String userId) {
                throw new RuntimeException("User service is unavailable");
            }
        };
    }
}
