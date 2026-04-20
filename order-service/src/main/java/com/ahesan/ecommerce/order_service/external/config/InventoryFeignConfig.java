package com.ahesan.ecommerce.order_service.external.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
public class InventoryFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new InventoryFeignErrorDecoder();
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String correlationId = MDC.get("correlationId");

            if (correlationId != null) {
                if (!template.headers().containsKey("X-Correlation-ID")) {
                    template.header("X-Correlation-ID", correlationId);
                }
            }
        };
    }
}

