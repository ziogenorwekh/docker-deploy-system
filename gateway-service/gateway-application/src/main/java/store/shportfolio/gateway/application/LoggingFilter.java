package store.shportfolio.gateway.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(2)
@Component
public class LoggingFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Incoming request: Method = {}, Path = {}, Headers = {}",
                request.getMethod(), request.getURI(), request.getHeaders());
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> log.info("Outgoing response: Status = {}",
                        exchange.getResponse().getStatusCode()));
    }
}
