package com.ahesan.ecommerce.order_service.external.service;

import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.response.ApiResponse;

import java.util.UUID;

public interface UserServiceClient {
    ApiResponse<AddressResponse> getAddressById(UUID addressId, String userId);
}
