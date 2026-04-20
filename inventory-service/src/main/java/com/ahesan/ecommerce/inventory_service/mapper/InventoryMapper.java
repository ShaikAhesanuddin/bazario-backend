package com.ahesan.ecommerce.inventory_service.mapper;

import com.ahesan.ecommerce.inventory_service.dto.ProductRequest;
import com.ahesan.ecommerce.inventory_service.dto.ProductResponse;
import com.ahesan.ecommerce.inventory_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    Product toEntity(ProductRequest request);

    @Mapping(target = "inStock", expression = "java(entity.getQuantity() > 0)")
    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "discountedPrice", expression = "java(calculateDiscountedPrice(entity))")
    ProductResponse toDto(Product entity);

    default BigDecimal calculateDiscountedPrice(Product product) {
        if (product.getDiscountPercentage() == null) {
            return product.getPrice();
        }

        return product.getPrice().subtract(
                product.getPrice()
                        .multiply(product.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100))
        );
    }

}
