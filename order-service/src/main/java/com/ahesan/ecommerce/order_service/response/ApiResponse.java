package com.ahesan.ecommerce.order_service.response;

import com.ahesan.ecommerce.order_service.dto.ErrorDetail;
import com.ahesan.ecommerce.order_service.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        ResponseStatus status,
        String message,
        T data,
        List<ErrorDetail> errors,
        String traceId,
        String correlationId,
        String path,
        LocalDateTime timestamp
) {
    public ApiResponse(ResponseStatus status, String message, T data) {
        this(
                status,
                message,
                data,
                null,
                getTraceId(),
                getCorrelationId(),
                getPath(),
                LocalDateTime.now()
        );
    }

    public ApiResponse(ResponseStatus status, String message, List<ErrorDetail> errors) {
        this(
                status,
                message,
                null,
                errors,
                getTraceId(),
                getCorrelationId(),
                getPath(),
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, message, data);
    }

    public static <T> ApiResponse<T> error(String message, List<ErrorDetail> errors) {
        return new ApiResponse<>(ResponseStatus.ERROR, message, errors);
    }

    private static String getTraceId() {
        return MDC.get("traceId");
    }

    private static String getCorrelationId() {
        return MDC.get("correlationId");
    }

    private static String getPath() {
        return MDC.get("path");
    }
}

