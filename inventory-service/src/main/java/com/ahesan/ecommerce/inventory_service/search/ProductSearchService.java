package com.ahesan.ecommerce.inventory_service.search;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductSearchService {

    List<ProductDocument> searchProducts(String query);

    List<ProductDocument> getTop100ProductsOrderByPriceDesc();

    ProductResponse getProductById(String productId);

    List<ProductResponse> getRelatedProducts(UUID productId);
}
