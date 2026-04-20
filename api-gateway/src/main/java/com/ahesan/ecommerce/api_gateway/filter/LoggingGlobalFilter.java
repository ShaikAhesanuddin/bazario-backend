package com.ahesan.ecommerce.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter{

    private static final String HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(HEADER);

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        String finalCorrelationId = correlationId;

        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.set(HEADER, finalCorrelationId))
                .build();

        var mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .add(HEADER, finalCorrelationId);

        MDC.put("correlationId", finalCorrelationId);

        log.info("Incoming Request → {} {}",
                mutatedExchange.getRequest().getMethod(),
                mutatedExchange.getRequest().getURI());

        return chain.filter(mutatedExchange)

                .then(Mono.fromRunnable(() -> {


                    long duration = System.currentTimeMillis() - startTime;

                    log.info("Response → Status: {} | Time: {} ms",
                            mutatedExchange.getResponse().getStatusCode(),
                            duration);
                }));
    }
}