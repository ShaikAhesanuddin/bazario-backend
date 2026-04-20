package com.ahesan.ecommerce.auth_service.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
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


