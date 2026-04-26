package com.ahesan.ecommerce.order_service.exception;

import com.ahesan.ecommerce.order_service.dto.ErrorDetail;
import com.ahesan.ecommerce.order_service.enums.ErrorType;
import com.ahesan.ecommerce.order_service.response.ApiResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    private List<ErrorDetail> buildError(ErrorType type, String message) {
        return List.of(new ErrorDetail(type, message));
    }

    private ResponseEntity<ApiResponse<Object>> buildResponse(
            HttpStatus status,
            String userMessage,
            ErrorType type,
            String internalMessage
    ) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(
                        userMessage,
                        buildError(type, internalMessage)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {

        List<ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(
                        ErrorType.VALIDATION,
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {

        List<ErrorDetail> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> new ErrorDetail(
                        ErrorType.VALIDATION,
                        v.getPropertyPath().toString(),
                        v.getMessage()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {

        log.warn("Invalid JSON request", ex);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body",
                ErrorType.VALIDATION,
                "Malformed JSON request"
        );
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAddressNotFound(AddressNotFoundException ex) {

        log.warn("Address not found: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Address not found. Please check your saved addresses.",
                ErrorType.BUSINESS,
                ex.getMessage()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOrderNotFound(OrderNotFoundException ex) {

        log.warn("Order not found: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Order not found.",
                ErrorType.BUSINESS,
                ex.getMessage()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Object>> handleStockException(InsufficientStockException ex) {

        log.warn("Stock issue: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Insufficient stock for the selected product.",
                ErrorType.BUSINESS,
                ex.getMessage()
        );
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleRateLimit(RateLimitExceededException ex) {

        log.warn("Rate limit exceeded: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too many requests. Please try again later.",
                ErrorType.BUSINESS,
                ex.getMessage()
        );
    }


    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserServiceException(UserServiceException ex) {

        log.error("User service error: {}", ex.getMessage());

        if (ex.getStatus() >= 400 && ex.getStatus() < 500) {
            return buildResponse(
                    HttpStatus.BAD_REQUEST,
                    "We couldn't process your request. Please verify your details.",
                    ErrorType.BUSINESS,
                    ex.getMessage()
            );
        }

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Address service is currently unavailable. Please try again later.",
                ErrorType.EXTERNAL,
                "User service unavailable"
        );
    }

    @ExceptionHandler(InventoryServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleInventoryException(InventoryServiceException ex) {

        log.error("Inventory service error: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to process order at the moment. Please try again later.",
                ErrorType.EXTERNAL,
                ex.getMessage()
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Object>> handleFeign(FeignException ex) {

        log.error("Unhandled Feign error: {}", ex.getMessage());

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "A dependent service is unavailable. Please try again later.",
                ErrorType.EXTERNAL,
                "Feign client error"
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {

        log.error("Unexpected error occurred", ex);
        ex.printStackTrace();

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.",
                ErrorType.SYSTEM,
                ex.getMessage()
        );
    }
}