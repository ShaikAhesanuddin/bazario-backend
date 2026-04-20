package com.ahesan.ecommerce.inventory_service.mapper;

import com.ahesan.ecommerce.inventory_service.document.ProductDocument;
import com.ahesan.ecommerce.inventory_service.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductSearchMapper {

    public ProductDocument toDocument(Product product) {
        ProductDocument doc = new ProductDocument();

        doc.setProductId(product.getId().toString());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setBrand(product.getBrand());
        doc.setPrice(calculatePrice(product));
        doc.setImageUrl(product.getImageUrl());

        return doc;
    }

    private BigDecimal calculatePrice(Product product) {
        if (product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        if (product.getDiscountPercentage() == null) {
            return product.getPrice();
        }

        BigDecimal discount = product.getPrice()
                .multiply(product.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        return product.getPrice().subtract(discount);
    }
}

