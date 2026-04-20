package com.ahesan.ecommerce.order_service.external.service.impl;

import com.ahesan.ecommerce.order_service.exception.InventoryServiceException;
import com.ahesan.ecommerce.order_service.external.client.InventoryClient;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.dto.ReduceStockRequest;
import com.ahesan.ecommerce.order_service.external.dto.UpdateStockRequest;
import com.ahesan.ecommerce.order_service.external.service.InventoryServiceClient;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceClientImpl implements InventoryServiceClient {

    private final InventoryClient inventoryClient;

    @Override
    @CircuitBreaker(name = "inventoryService")
    @Retry(name = "inventoryService")
    public ApiResponse<InventoryResponse> getStock(String productId) {
        return inventoryClient.getStock(productId);
    }

    @Override
    @CircuitBreaker(name = "inventoryService")
    @Retry(name = "inventoryService")
    public ApiResponse<InventoryResponse> reduceStock(ReduceStockRequest request) {
        return inventoryClient.reduceStock(request);
    }

    @Override
    @CircuitBreaker(name = "inventoryService")
    @Retry(name = "inventoryService")
    public ApiResponse<InventoryResponse> addStock(UpdateStockRequest request) {
        return inventoryClient.addStock(request);
    }


//    public ApiResponse<InventoryResponse> getStockFallback(String skuCode, Throwable t) {
//        throw new InventoryServiceException("Inventory service unavailable (getStock)");
//    }
//
//    public ApiResponse<InventoryResponse> reduceStockFallback(ReduceStockRequest request, Throwable t) {
//        throw new InventoryServiceException("Inventory service unavailable (reduceStock)");
//    }
//
//    public ApiResponse<InventoryResponse> addStockFallback(UpdateStockRequest request, Throwable t) {
//        throw new InventoryServiceException("Inventory service unavailable (addStock)");
//    }
}
