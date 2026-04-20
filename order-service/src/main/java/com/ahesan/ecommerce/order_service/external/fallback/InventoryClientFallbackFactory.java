package com.ahesan.ecommerce.order_service.external.fallback;

import com.ahesan.ecommerce.order_service.dto.ErrorDetail;
import com.ahesan.ecommerce.order_service.enums.ErrorType;
import com.ahesan.ecommerce.order_service.external.client.InventoryClient;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.dto.ReduceStockRequest;
import com.ahesan.ecommerce.order_service.external.dto.UpdateStockRequest;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    private List<ErrorDetail> buildError() {
        return List.of(
                new ErrorDetail(
                        ErrorType.SYSTEM,
                        "Service is temporarily unavailable"
                )
        );
    }

    @Override
    public InventoryClient create(Throwable cause) {

        log.error("Inventory service fallback triggered", cause);

        return new InventoryClient() {

            @Override
            public ApiResponse<InventoryResponse> getStock(String productId) {
                return ApiResponse.error(
                        "We're unable to check product availability right now. Please try again shortly.",
                        buildError()
                );
            }

            @Override
            public ApiResponse<InventoryResponse> reduceStock(ReduceStockRequest request) {
                return ApiResponse.error(
                        "We couldn't process your order at the moment. Please try again in a few minutes.",
                        buildError()
                );
            }

            @Override
            public ApiResponse<InventoryResponse> addStock(UpdateStockRequest request) {
                return ApiResponse.error(
                        "We're unable to update stock right now. Please try again later.",
                        buildError()
                );
            }
        };
    }
}