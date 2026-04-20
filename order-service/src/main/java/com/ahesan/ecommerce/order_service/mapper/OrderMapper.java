package com.ahesan.ecommerce.order_service.mapper;

import com.ahesan.ecommerce.order_service.dto.OrderRequest;
import com.ahesan.ecommerce.order_service.dto.OrderResponse;
import com.ahesan.ecommerce.order_service.entity.Order;
import com.ahesan.ecommerce.order_service.external.dto.ReduceStockRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "id", target = "orderId")
    OrderResponse toResponse(Order order);

    ReduceStockRequest toReduceStockRequest(OrderRequest request);
}
