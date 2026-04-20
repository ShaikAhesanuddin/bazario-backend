package com.ahesan.ecommerce.order_service.exception;

public class InvalidOrderException extends OrderServiceException {
    public InvalidOrderException(String message) {
        super(message);
    }
}
