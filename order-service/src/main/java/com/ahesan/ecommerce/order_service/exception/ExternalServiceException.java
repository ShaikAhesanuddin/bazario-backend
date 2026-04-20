package com.ahesan.ecommerce.order_service.exception;

import lombok.Getter;

@Getter
public class ExternalServiceException extends RuntimeException {

    private final int status;

    public ExternalServiceException(String message, int status) {
        super(message);
        this.status = status;
    }

}