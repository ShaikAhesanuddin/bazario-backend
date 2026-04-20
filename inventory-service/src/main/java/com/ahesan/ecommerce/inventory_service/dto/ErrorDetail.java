package com.ahesan.ecommerce.inventory_service.dto;

import com.ahesan.ecommerce.inventory_service.enums.ErrorType;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetail(
        ErrorType type,
        String field,
        String message
) {
    public ErrorDetail(ErrorType type, String message) {
        this(type, null, message);
    }

    public ErrorDetail(ErrorType type, String field, String message) {
        this.type = type;
        this.field = field;
        this.message = message;
    }
}
