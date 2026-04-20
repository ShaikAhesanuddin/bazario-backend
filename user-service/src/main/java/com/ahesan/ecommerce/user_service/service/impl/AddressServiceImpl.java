package com.ahesan.ecommerce.user_service.service.impl;

import com.ahesan.ecommerce.user_service.dto.AddressRequest;
import com.ahesan.ecommerce.user_service.dto.AddressResponse;
import com.ahesan.ecommerce.user_service.entity.Address;
import com.ahesan.ecommerce.user_service.entity.User;
import com.ahesan.ecommerce.user_service.exception.AddressNotFoundException;
import com.ahesan.ecommerce.user_service.exception.UserNotFoundException;
import com.ahesan.ecommerce.user_service.mapper.AddressMapper;
import com.ahesan.ecommerce.user_service.repository.AddressRepository;
import com.ahesan.ecommerce.user_service.repository.UserRepository;
import com.ahesan.ecommerce.user_service.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepo;
    private final UserRepository userRepo;
    private final AddressMapper addressMapper;


    @Override
    public AddressResponse addAddress(UUID userId, AddressRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (request.isDefault()) {
            addressRepo.clearDefaultForUser(userId);
        }

        Address entity = addressMapper.toEntity(request);
        entity.setUser(user);
        Address savedAddress = addressRepo.save(entity);
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getAddresses(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Address> allAddress = addressRepo.findByUserId(userId);
        if (allAddress.isEmpty()) {
            throw new AddressNotFoundException("No address found for the userId: " + userId);
        }
        return allAddress.stream()
                .map(addressMapper::toResponse)
                .toList();

    }

    @Override
    public AddressResponse getAddressById(UUID userId, UUID addressId) {

        return addressRepo.findByIdAndUserId(addressId, userId)
                .map(addressMapper::toResponse)
                .orElseThrow(() -> new AddressNotFoundException("Address not found or not owned by user"));


    }

    @Override
    public void deleteAddress(UUID userId, UUID addressId) {

        Address address = addressRepo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found or not owned by user"));

        boolean wasDefault = address.isDefaultAddress();

        addressRepo.delete(address);

        if (wasDefault) {
            addressRepo.findTopByUserId(userId)
                    .ifPresent(a -> {
                        a.setDefaultAddress(true);
                        addressRepo.save(a);
                    });
        }

        log.info("Address {} deleted for user {}", addressId, userId);
    }

    @Override
    public void setDefaultAddress(UUID userId, UUID addressId) {

        addressRepo.clearDefaultForUser(userId);

        Address address = addressRepo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));

        address.setDefaultAddress(true);

        log.info("Address {} set as default for user {}", addressId, userId);
    }

}
