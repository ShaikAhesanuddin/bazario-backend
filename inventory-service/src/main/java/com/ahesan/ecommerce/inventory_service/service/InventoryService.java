package com.ahesan.ecommerce.inventory_service.service;

import com.ahesan.ecommerce.inventory_service.dto.ProductRequest;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;

import java.util.List;

public interface InventoryService {

    List<ProductResponse> getAllProducts();

    ProductResponse addProduct(ProductRequest request);

    ProductResponse getStock(String skuCode);

    ProductResponse reduceStock(String skuCode, Integer quantity);

    ProductResponse addStock(String skuCode, Integer quantity);
}
