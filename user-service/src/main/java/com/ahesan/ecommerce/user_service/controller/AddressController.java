package com.ahesan.ecommerce.user_service.controller;

import com.ahesan.ecommerce.user_service.dto.AddressRequest;
import com.ahesan.ecommerce.user_service.dto.AddressResponse;
import com.ahesan.ecommerce.user_service.response.ApiResponse;
import com.ahesan.ecommerce.user_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "/api/v1/users/me/addresses",
        produces = {
                "application/json",
                "application/xml"
        }
)
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid
            @RequestBody AddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Address added successfully",
                                addressService.addAddress(userId, request)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Addresses fetched successfully",
                        addressService.getAddresses(userId)
                )
        );
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressesById(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("addressId") UUID addressId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Addresses fetched successfully",
                        addressService.getAddressById(userId, addressId)
                )
        );
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("addressId") UUID addressId
    ) {
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(
                ApiResponse.success("Default address set successfully", null)
        );
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable("addressId") UUID addressId
    ) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }


}
