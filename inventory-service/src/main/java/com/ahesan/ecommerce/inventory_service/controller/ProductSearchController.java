package com.ahesan.ecommerce.inventory_service.controller;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;
import com.ahesan.ecommerce.inventory_service.exception.ProductNotFoundException;
import com.ahesan.ecommerce.inventory_service.response.ApiResponse;
import com.ahesan.ecommerce.inventory_service.search.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(

        value = "/api/v1/products",
        produces = {
                "application/json",
                "application/xml"
        }
)
@RequiredArgsConstructor
@Slf4j
public class ProductSearchController {

    private final ProductSearchService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDocument>>> search(

            @RequestParam(name = "query", required = false) String query
    ) {

        log.info("Searching products for query: {}", query);
        List<ProductDocument> products;
        if (query == null || query.trim().isEmpty()) {
            products = service.getTop100ProductsOrderByPriceDesc();

        } else {
            products = service.searchProducts(query);
        }

        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                "Products fetched successfully",
                                products
                        )
                );
    }


    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable("productId") UUID productId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                "Product fetched successfully",
                                service.getProductById(productId.toString())
                        )
                );
    }

    @GetMapping("/{productId}/related")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getRelatedProducts(
            @PathVariable UUID productId
    ) {
        log.info("Fetching related products for product ID: {}", productId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Related products fetched",
                        service.getRelatedProducts(productId)
                )
        );
    }
}
