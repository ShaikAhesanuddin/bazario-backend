package com.ahesan.ecommerce.order_service.exception;

public class InsufficientStockException extends OrderServiceException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
