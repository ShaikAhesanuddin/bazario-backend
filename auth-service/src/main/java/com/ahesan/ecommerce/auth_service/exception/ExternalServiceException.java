package com.ahesan.ecommerce.auth_service.exception;


public class ExternalServiceException extends RuntimeException {

    private final int status;

    public ExternalServiceException(String message, int status) {
        super(message);
        this.status = status;
    }

    public ExternalServiceException(String message, int status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
