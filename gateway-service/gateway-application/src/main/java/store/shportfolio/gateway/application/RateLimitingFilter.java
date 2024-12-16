package store.shportfolio.gateway.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import store.shportfolio.gateway.application.config.InMemoryRateLimiter;

@Order(1)
@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<Object> {

    private final InMemoryRateLimiter rateLimiter;

    @Autowired
    public RateLimitingFilter(InMemoryRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // 사용자 고유 키 (IP, UserID 등) 추출
            String userKey = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();

            if (!rateLimiter.isAllowed(userKey)) {
                return Mono.fromRunnable(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                });
            }

            return chain.filter(exchange);
        };
    }
}
