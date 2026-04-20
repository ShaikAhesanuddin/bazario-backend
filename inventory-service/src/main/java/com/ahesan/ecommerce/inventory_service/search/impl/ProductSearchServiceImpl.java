package com.ahesan.ecommerce.inventory_service.search.impl;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;
import com.ahesan.ecommerce.inventory_service.entity.Product;
import com.ahesan.ecommerce.inventory_service.exception.ProductNotFoundException;
import com.ahesan.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ahesan.ecommerce.inventory_service.repository.ProductRepository;
import com.ahesan.ecommerce.inventory_service.repository.ProductSearchRepository;
import com.ahesan.ecommerce.inventory_service.search.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchServiceImpl implements ProductSearchService {
    private final ProductSearchRepository productSearchRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductRepository productRepository;

    @Override
    public List<ProductDocument> searchProducts(String query) {
        log.info("Searching products for query: {}", query);
        query = sanitizeQuery(query);
        log.info("Sanitized query: {}", query);
        if (query.isBlank()) {
            return getTop100ProductsOrderByPriceDesc();
        }
        List<ProductDocument> products = productSearchRepository
                .findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, query);
        log.info("Found {} products for query: {}", products.size(), query);
        return products;

    }

    @Override
    public List<ProductDocument> getTop100ProductsOrderByPriceDesc() {
        log.info("Fetching top 100 products by price");
        List<ProductDocument> products = productSearchRepository.findTop100ByOrderByPriceDesc();
        log.info("Fetched {} products", products.size());
        return products;
    }

    @Override
    public ProductResponse getProductById(String productId) {
        log.info("Fetching product by ID: {}", productId);
        UUID id = UUID.fromString(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found for ID: " + productId));
        return inventoryMapper.toDto(product);

    }

    @Override
    public List<ProductResponse> getRelatedProducts(UUID productId) {
        log.info("Fetching related products for product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return productRepository
                .findTop8ByCategoryAndIdNot(product.getCategory(), productId)
                .stream()
                .map(inventoryMapper::toDto)
                .toList();
    }

    public String sanitizeQuery(String query) {

        if (query == null) return "";

        return query
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
