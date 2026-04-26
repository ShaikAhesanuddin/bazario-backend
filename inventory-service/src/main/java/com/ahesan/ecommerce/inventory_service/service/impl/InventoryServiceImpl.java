package com.ahesan.ecommerce.inventory_service.service.impl;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import com.ahesan.ecommerce.inventory_service.dto.ProductRequest;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;
import com.ahesan.ecommerce.inventory_service.entity.Product;
import com.ahesan.ecommerce.inventory_service.exception.DuplicateProductException;
import com.ahesan.ecommerce.inventory_service.exception.InsufficientStockException;
import com.ahesan.ecommerce.inventory_service.exception.ProductNotFoundException;
import com.ahesan.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ahesan.ecommerce.inventory_service.mapper.ProductSearchMapper;
import com.ahesan.ecommerce.inventory_service.repository.ProductRepository;
import com.ahesan.ecommerce.inventory_service.repository.ProductSearchRepository;
import com.ahesan.ecommerce.inventory_service.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper productMapper;
    private final ProductRepository productRepository;
    private final ProductSearchMapper productSearchMapper;
    private final ProductSearchRepository productSearchRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> productList = productRepository.findAll();

        return productList.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {

        log.info("Adding product with SKU: {}", request.skuCode());

        if (productRepository.existsBySkuCode(request.skuCode())) {
            throw new DuplicateProductException("Product already exists for SKU: " + request.skuCode());
        }

        Product entity = productMapper.toEntity(request);

        if (entity.getActive() == null) {
            entity.setActive(true);
        }

        Product savedProduct = productRepository.save(entity);

        log.info("Product created successfully | productId: {}", savedProduct.getId());

        indexProduct(savedProduct);

        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductResponse getStock(String productId) {

        log.info("Fetching stock for productId: {}", productId);

        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found for productId: " + productId));

        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponse reduceStock(String productId, Integer quantity) {

        log.info("Reducing stock for productId: {} by {}", productId, quantity);

        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found for productId: " + productId));

        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for productId: " + productId);
        }

        product.setQuantity(product.getQuantity() - quantity);

        Product updatedProduct = productRepository.save(product);

        log.info("Stock updated for productId: {}", productId);

        indexProduct(updatedProduct);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponse addStock(String productId, Integer quantity) {

        log.info("Adding stock | productId: {} | quantity: {}", productId, quantity);

        Product product = productRepository.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found for productId: " + productId));

        product.setQuantity(product.getQuantity() + quantity);

        Product updatedProduct = productRepository.save(product);

        log.info("Stock added successfully for productId: {}", productId);

        indexProduct(updatedProduct);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void reindexAllProducts() {

        log.info("Starting full reindex...");

        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            indexProduct(product);
        }

        log.info("Reindex completed. Total: {}", products.size());
    }

    private void indexProduct(Product product) {
        try {
            ProductDocument doc = productSearchMapper.toDocument(product);
            productSearchRepository.save(doc);
            log.info("Product indexed successfully for productId: {}", product.getId());
        } catch (Exception e) {
            log.error("Failed to index product for productId: {}", product.getId(), e);
        }
    }
}