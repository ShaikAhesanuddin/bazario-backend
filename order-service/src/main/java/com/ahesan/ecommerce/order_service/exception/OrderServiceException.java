package com.ahesan.ecommerce.order_service.exception;

public abstract class OrderServiceException extends RuntimeException {
    public OrderServiceException(String message) {
        super(message);
    }
}
