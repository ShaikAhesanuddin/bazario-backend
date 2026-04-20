package com.ahesan.ecommerce.order_service.external.client;

import com.ahesan.ecommerce.order_service.external.config.InventoryFeignConfig;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.dto.ReduceStockRequest;
import com.ahesan.ecommerce.order_service.external.dto.UpdateStockRequest;
import com.ahesan.ecommerce.order_service.external.fallback.InventoryClientFallbackFactory;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "INVENTORY-SERVICE",
        path = "/api/v1/inventory",
        configuration = InventoryFeignConfig.class,
        fallbackFactory = InventoryClientFallbackFactory.class)

public interface InventoryClient {


    @GetMapping("/{id}")
    ApiResponse<InventoryResponse> getStock(@PathVariable("id") String productId);

    @PutMapping("/reduce")
    ApiResponse<InventoryResponse> reduceStock(@RequestBody ReduceStockRequest request);

    @PutMapping("/add")
    ApiResponse<InventoryResponse> addStock(@RequestBody UpdateStockRequest request);
}