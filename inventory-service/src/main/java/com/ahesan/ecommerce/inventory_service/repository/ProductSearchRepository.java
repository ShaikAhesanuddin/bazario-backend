package com.ahesan.ecommerce.inventory_service.repository;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    List<ProductDocument> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String brand, String description);

    List<ProductDocument> findTop100ByOrderByPriceDesc();
}
