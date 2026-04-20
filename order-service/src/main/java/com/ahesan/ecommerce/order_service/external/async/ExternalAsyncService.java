package com.ahesan.ecommerce.order_service.external.async;

import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.service.InventoryServiceClient;
import com.ahesan.ecommerce.order_service.external.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalAsyncService {

    private final InventoryServiceClient inventoryClient;
    private final UserServiceClient userClient;

    @Async("asyncExecutor")
    public CompletableFuture<InventoryResponse> fetchProductAsync(String productId) {
        try {
            return CompletableFuture.completedFuture(
                    inventoryClient.getStock(productId).data()
            );
        } catch (Exception ex) {
            log.error("Failed to fetch product {}", productId, ex);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<AddressResponse> fetchAddressAsync(String addressId, String userId) {

        UUID uuid;
        try {
            uuid = UUID.fromString(addressId);
        } catch (Exception ex) {
            log.warn("Invalid addressId format: {}", addressId);
            return CompletableFuture.completedFuture(null);
        }

        try {
            var response = userClient.getAddressById(uuid, userId);

            if (response == null || response.data() == null) {
                log.warn("Address not found for addressId: {}", addressId);
                return CompletableFuture.completedFuture(null);
            }

            return CompletableFuture.completedFuture(response.data());

        } catch (Exception ex) {
            log.error("Failed to fetch address {}", addressId, ex);
            return CompletableFuture.completedFuture(null);
        }
    }
}