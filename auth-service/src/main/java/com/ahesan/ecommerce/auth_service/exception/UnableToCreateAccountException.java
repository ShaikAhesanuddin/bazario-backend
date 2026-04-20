package com.ahesan.ecommerce.auth_service.exception;

public class UnableToCreateAccountException extends RuntimeException {
    public UnableToCreateAccountException(String message) {
        super(message);
    }

    public UnableToCreateAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
