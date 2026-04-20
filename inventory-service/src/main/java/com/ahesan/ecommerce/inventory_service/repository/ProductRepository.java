package com.ahesan.ecommerce.inventory_service.repository;

import com.ahesan.ecommerce.inventory_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySkuCode(String skuCode);

    List<Product> findTop8ByCategoryAndIdNot(String category, UUID id);
}
