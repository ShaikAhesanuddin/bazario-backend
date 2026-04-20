package com.ahesan.ecommerce.inventory_service.controller;

import com.ahesan.ecommerce.inventory_service.dto.ProductRequest;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;
import com.ahesan.ecommerce.inventory_service.dto.ReduceStockRequest;
import com.ahesan.ecommerce.inventory_service.dto.UpdateStockRequest;
import com.ahesan.ecommerce.inventory_service.response.ApiResponse;
import com.ahesan.ecommerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/v1/inventory",
        produces = {
                "application/json",
                "application/xml"
        }
)
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> response = service.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success("Products fetched successfully", response)
                );

    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid
            @RequestBody ProductRequest request
    ) {
        ProductResponse response = service.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success("Product added successfully", response)
                );

    }

    @GetMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> getStock(
            @PathVariable String skuCode
    ) {
        ProductResponse response = service.getStock(skuCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success("Stock fetched successfully", response)
                );
    }

    @PutMapping("/reduce")
    public ResponseEntity<ApiResponse<ProductResponse>> reduceStock(
            @Valid
            @RequestBody ReduceStockRequest request
    ) {
        ProductResponse response = service.reduceStock(request.productId(), request.quantity());

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success("Stock reduced successfully", response)
                );
    }

    @PutMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponse>> addStock(
            @Valid
            @RequestBody UpdateStockRequest request
    ) {
        ProductResponse response = service.addStock(request.productId(), request.quantity());

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success("Stock added successfully", response)
                );
    }

}
