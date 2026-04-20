package com.ahesan.ecommerce.order_service.service.impl;

import com.ahesan.ecommerce.order_service.dto.OrderRequest;
import com.ahesan.ecommerce.order_service.dto.OrderResponse;
import com.ahesan.ecommerce.order_service.entity.Order;
import com.ahesan.ecommerce.order_service.enums.OrderStatus;
import com.ahesan.ecommerce.order_service.exception.AddressNotFoundException;
import com.ahesan.ecommerce.order_service.exception.InsufficientStockException;
import com.ahesan.ecommerce.order_service.exception.InventoryServiceException;
import com.ahesan.ecommerce.order_service.exception.RateLimitExceededException;
import com.ahesan.ecommerce.order_service.external.async.ExternalAsyncService;
import com.ahesan.ecommerce.order_service.external.dto.AddressResponse;
import com.ahesan.ecommerce.order_service.external.dto.InventoryResponse;
import com.ahesan.ecommerce.order_service.external.dto.UpdateStockRequest;
import com.ahesan.ecommerce.order_service.external.service.InventoryServiceClient;
import com.ahesan.ecommerce.order_service.external.service.UserServiceClient;
import com.ahesan.ecommerce.order_service.mapper.OrderMapper;
import com.ahesan.ecommerce.order_service.repository.OrderRepository;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import com.ahesan.ecommerce.order_service.service.OrderService;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryClient;
    private final UserServiceClient userClient;
    private final OrderMapper mapper;
    private final ExternalAsyncService asyncService;

    @Override
    @RateLimiter(name = "orderRateLimiter", fallbackMethod = "fallbackRateLimiter")
    public OrderResponse placeOrder(OrderRequest request, UUID userId) {

        log.info("Placing order | ProductId: {} | Quantity: {}", request.productId(), request.quantity());


        InventoryResponse inventory = fetchAndValidateInventory(request);

        log.info("Inventory fetched successfully | ProductId: {}", request.productId());
        inventoryClient.reduceStock(mapper.toReduceStockRequest(request));
        log.info("Stock reduced successfully | ProductId: {}", request.productId());

        Optional<AddressResponse> addressResponse = fetchAddressForOrder(request.addressId(), userId.toString());
        if (addressResponse.isEmpty()) {
            log.warn("User service unavailable, creating PENDING order...");
        }

        try {
            return createOrder(request, inventory, userId, addressResponse);

        } catch (Exception ex) {

            log.error("Order creation failed, triggering compensation | ProductId: {}", request.productId(), ex);

            compensateStock(request);

            throw ex;
        }
    }


    @Override
    public List<OrderResponse> getAllOrders(UUID userId) {

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId.toString());

        return orders.stream()
                .map(order -> {

                    String productId = order.getProductId();
                    String addressId = order.getAddressId();

                    CompletableFuture<InventoryResponse> productFuture =
                            asyncService.fetchProductAsync(productId)
                                    .orTimeout(2, TimeUnit.SECONDS)
                                    .exceptionally(ex -> {
                                        log.warn("Product fetch failed for productId: {}", productId, ex);
                                        return fallbackProduct(productId);
                                    });

                    CompletableFuture<AddressResponse> addressFuture =
                            asyncService.fetchAddressAsync(addressId, userId.toString())
                                    .orTimeout(2, TimeUnit.SECONDS)
                                    .exceptionally(ex -> {
                                        log.warn("Address fetch failed for addressId: {}", addressId, ex);
                                        return fallbackAddress(addressId);
                                    });


                    CompletableFuture.allOf(productFuture, addressFuture).join();

                    InventoryResponse product = productFuture.getNow(null);
                    AddressResponse address = addressFuture.getNow(null);

                    return new OrderResponse(
                            order.getId(),
                            order.getQuantity(),
                            order.getPrice(),
                            order.getTotalAmount(),
                            order.getOrderStatus(),
                            product,
                            address,
                            order.getCreatedAt()
                    );
                })
                .toList();
    }


    private InventoryResponse fetchAndValidateInventory(OrderRequest request) {

        ApiResponse<InventoryResponse> response =
                inventoryClient.getStock(request.productId());

        if (response == null || response.data() == null) {
            throw new InventoryServiceException("Invalid response from inventory service");
        }
        InventoryResponse inventory = response.data();

        if (Boolean.FALSE.equals(inventory.active())) {
            throw new InventoryServiceException("Product is inactive");
        }

        return inventory;
    }

    private Optional<AddressResponse> fetchAddressForOrder(String addressId, String userId) {

        log.info("Fetching address for addressId: {}", addressId);

        try {
            ApiResponse<AddressResponse> response =
                    userClient.getAddressById(UUID.fromString(addressId), userId);

            if (response == null || response.data() == null) {
                throw new AddressNotFoundException("Address not found");
            }

            return Optional.of(response.data());

        } catch (AddressNotFoundException ex) {
            log.error("Invalid addressId: {}", addressId);
            throw ex;

        } catch (feign.RetryableException ex) {
            log.warn("User service unavailable, creating PENDING order for addressId: {}", addressId);
            return Optional.empty();

        } catch (Exception ex) {
            log.error("Unexpected error while fetching address", ex);
            return Optional.empty();
        }
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, InventoryResponse inventory, UUID userId, Optional<AddressResponse> address) {

        BigDecimal totalAmount = inventory.price()
                .multiply(BigDecimal.valueOf(request.quantity()));
        OrderStatus orderStatus = address.isPresent() ? OrderStatus.CREATED : OrderStatus.PENDING;

        Order order = Order.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .price(inventory.price())
                .orderStatus(orderStatus)
                .totalAmount(totalAmount)
                .userId(userId.toString())
                .addressId(request.addressId())
                .build();

        Order saved = orderRepository.save(order);

        log.info("Order created successfully | OrderId: {}", saved.getId());

        return new OrderResponse(
                saved.getId(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getTotalAmount(),
                saved.getOrderStatus(),
                inventory,
                address.orElse(null),
                saved.getCreatedAt()
        );

    }

    private void compensateStock(OrderRequest request) {
        try {
            inventoryClient.addStock(
                    new UpdateStockRequest(request.productId(), request.quantity())
            );
            log.info("Stock compensated successfully for ProductId: {}", request.productId());
        } catch (Exception ex) {
            log.error("CRITICAL: Stock compensation failed for ProductId: {}", request.productId(), ex);
        }
    }

    private OrderResponse fallbackRateLimiter(OrderRequest request, UUID userId, Throwable ex) {

        log.info("fallbackRateLimiter triggered");

        if (ex instanceof RequestNotPermitted) {
            throw new RateLimitExceededException(
                    "You can only place an order once every 10 seconds"
            );
        }

        Throwable root = getRootCause(ex);

        if (root instanceof InsufficientStockException) {
            log.info("Root cause is InsufficientStockException");
            throw (InsufficientStockException) root;
        }

        if (root instanceof InventoryServiceException) {
            log.info("Root cause is InventoryServiceException");
            throw (InventoryServiceException) root;
        }

        if (root.getMessage() != null &&
                root.getMessage().contains("Load balancer does not contain an instance")) {

            log.info("Inventory service unavailable");

            throw new InventoryServiceException(
                    "Inventory service is down or unavailable"
            );
        }

        log.error("Unexpected error in fallbackRateLimiter", ex);
        throw new RuntimeException(ex);
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    private InventoryResponse fallbackProduct(String productId) {
        return new InventoryResponse(
                productId,
                "Unavailable",
                null,
                0,
                BigDecimal.ZERO,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }

    private AddressResponse fallbackAddress(String addressId) {
        return new AddressResponse(
                UUID.fromString(addressId),
                "Unavailable",
                null,
                null,
                null,
                null,
                null
        );
    }
}