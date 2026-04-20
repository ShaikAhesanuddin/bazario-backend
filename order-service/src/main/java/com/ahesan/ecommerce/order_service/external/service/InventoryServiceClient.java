package com.ahesan.ecommerce.order_service.external.service;

import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.dto.ReduceStockRequest;
import com.ahesan.ecommerce.order_service.external.dto.UpdateStockRequest;
import com.ahesan.ecommerce.order_service.response.ApiResponse;

public interface InventoryServiceClient {

    ApiResponse<InventoryResponse> getStock(String productId);

    ApiResponse<InventoryResponse> reduceStock(ReduceStockRequest request);

    ApiResponse<InventoryResponse> addStock(UpdateStockRequest request);
}
