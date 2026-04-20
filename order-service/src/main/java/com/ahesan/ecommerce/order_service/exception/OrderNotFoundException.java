package com.ahesan.ecommerce.order_service.exception;

public class OrderNotFoundException extends OrderServiceException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
