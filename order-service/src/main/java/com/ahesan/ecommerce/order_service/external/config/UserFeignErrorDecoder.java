package com.ahesan.ecommerce.order_service.external.config;

import com.ahesan.ecommerce.order_service.exception.AddressNotFoundException;
import com.ahesan.ecommerce.order_service.exception.UserServiceException;
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
public class UserFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        int status = response.status();
        String body = extractBody(response);
        String message = extractMessage(body);

        logError(methodKey, response, message, body);

        if (status == 404) {
            return new AddressNotFoundException(message);
        }

        if (status >= 400 && status < 500) {
            return new UserServiceException(message, status);
        }


        if (status >= 500) {
            return new RetryableException(
                    status,
                    message,
                    response.request().httpMethod(),
                    null,
                    (Long) null,
                    response.request()
            );
        }

        return new UserServiceException("Unexpected error from user service", status);
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
            return "User service error";
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

    private void logError(String methodKey, Response response, String message, String body) {
        int status = response.status();

        if (status >= 500) {
            log.error(
                    "UserFeign Error | method={} | status={} | message={} | body={}",
                    methodKey, status, message, body
            );
        } else {
            log.warn(
                    "UserFeign Client Error | method={} | status={} | message={}",
                    methodKey, status, message
            );
        }
    }
}