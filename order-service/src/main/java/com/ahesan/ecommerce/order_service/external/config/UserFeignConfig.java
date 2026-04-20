package com.ahesan.ecommerce.order_service.external.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
public class UserFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserFeignErrorDecoder();
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

    @Bean
    public RequestInterceptor userContextInterceptor() {
        return requestTemplate -> {

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                String userId = attributes.getRequest().getHeader("X-User-Id");
                String role = attributes.getRequest().getHeader("X-User-Role");

                if (userId != null) {
                    requestTemplate.header("X-User-Id", userId);
                }

                if (role != null) {
                    requestTemplate.header("X-User-Role", role);
                }
            }
        };
    }

}
