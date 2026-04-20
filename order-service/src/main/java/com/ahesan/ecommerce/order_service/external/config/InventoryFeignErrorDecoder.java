package com.ahesan.ecommerce.order_service.external.config;

import com.ahesan.ecommerce.order_service.exception.ExternalServiceException;
import com.ahesan.ecommerce.order_service.exception.InsufficientStockException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class InventoryFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String url = response.request().url();
        String httpMethod = response.request().httpMethod().name();
        int status = response.status();

        String body = extractBody(response);

        String message = extractMessage(body);

        logStructuredError(methodKey, httpMethod, url, status, message, body);


        if (status == 400 && message.toLowerCase().contains("stock")) {
            return new InsufficientStockException(message);
        }


        if (status >= 400 && status < 500) {
            return new ExternalServiceException(message, status);
        }


        if (status >= 500) {
            return new RetryableException(
                    status,
                    message,
                    response.request().httpMethod(),
                    null,
                    (Long)null,
                    response.request()
            );
        }

        return new ExternalServiceException("Unexpected error", status);
    }



    private String extractBody(Response response) {
        if (response.body() == null) return "";

        try (InputStream is = response.body().asInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to read response body", e);
            return "";
        }
    }

    private String extractMessage(String body) {
        if (body == null || body.isBlank()) {
            return "External service error";
        }

        try {
            if (body.trim().startsWith("{")) {
                JsonNode json = objectMapper.readTree(body);
                if (json.has("message")) {
                    return json.get("message").asText();
                }
            }
            return body;
        } catch (Exception e) {
            return body;
        }
    }

    private void logStructuredError(String methodKey,
                                    String httpMethod,
                                    String url,
                                    int status,
                                    String message,
                                    String body) {

        if (status >= 500) {
            log.error(
                    "Feign Error | method={} | http={} | url={} | status={} | message={} | body={}",
                    methodKey, httpMethod, url, status, message, body
            );
        } else {
            log.warn(
                    "Feign Client Error | method={} | http={} | url={} | status={} | message={}",
                    methodKey, httpMethod, url, status, message
            );
        }
    }
}