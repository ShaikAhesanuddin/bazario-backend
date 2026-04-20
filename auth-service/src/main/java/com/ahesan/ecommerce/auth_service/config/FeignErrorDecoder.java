package com.ahesan.ecommerce.auth_service.config;

import com.ahesan.ecommerce.auth_service.exception.ExternalServiceException;
import com.ahesan.ecommerce.auth_service.exception.UserAlreadyExistsException;
import com.ahesan.ecommerce.auth_service.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = extractMessage(response);

        return switch (response.status()) {

            case 404 -> new UserNotFoundException(message);

            case 409 -> new UserAlreadyExistsException(message);

            case 400, 401, 403 -> new ExternalServiceException(message, response.status());

            default -> new ExternalServiceException(message, response.status());
        };
    }

    private String extractMessage(Response response) {
        try {
            if (response.body() == null) return "External service error";

            String body = new String(response.body().asInputStream().readAllBytes());

            if (body.trim().startsWith("{")) {
                JsonNode jsonNode = objectMapper.readTree(body);
                return jsonNode.has("message")
                        ? jsonNode.get("message").asText()
                        : "External service error";
            }

            return body;

        } catch (Exception e) {
            return "External service error";
        }
    }
}