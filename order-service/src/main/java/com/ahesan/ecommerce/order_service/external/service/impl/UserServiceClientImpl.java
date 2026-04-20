package com.ahesan.ecommerce.order_service.external.service.impl;

import com.ahesan.ecommerce.order_service.external.client.UserClient;
import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.external.service.UserServiceClient;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceClientImpl implements UserServiceClient {

    private final UserClient userClient;

    @Override
    public ApiResponse<AddressResponse> getAddressById(UUID addressId, String userId) {
        return userClient.getAddressById(addressId, userId);
    }
}
