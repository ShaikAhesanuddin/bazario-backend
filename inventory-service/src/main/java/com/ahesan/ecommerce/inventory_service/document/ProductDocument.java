package com.ahesan.ecommerce.inventory_service.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;

@Document(indexName = "products")
@Data
public class ProductDocument {

    @Id
    private String productId;

    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private String imageUrl;
}
