package com.ahesan.ecommerce.user_service.service;

import com.ahesan.ecommerce.user_service.dto.AddressRequest;
import com.ahesan.ecommerce.user_service.dto.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressResponse addAddress(UUID userId, AddressRequest request);

    List<AddressResponse> getAddresses(UUID userId);

    AddressResponse getAddressById(UUID userId, UUID addressId);

    void deleteAddress(UUID userId, UUID addressId);

    void setDefaultAddress(UUID userId, UUID addressId);
}

