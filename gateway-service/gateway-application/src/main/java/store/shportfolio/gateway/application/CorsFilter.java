package store.shportfolio.gateway.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import store.shportfolio.gateway.application.config.CorsConfigData;
@Slf4j
@Order(0)
@Component
public class CorsFilter extends AbstractGatewayFilterFactory<CorsConfigData> {

    public CorsFilter() {
        super(CorsConfigData.class);
    }

    @Override
    public GatewayFilter apply(CorsConfigData config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 모든 응답에 CORS 헤더 추가
            response.getHeaders().set("Access-Control-Allow-Origin", config.getAccessControlAllowOrigin());
            response.getHeaders().set("Access-Control-Allow-Methods", config.getAccessControlAllowMethods());
            response.getHeaders().set("Access-Control-Allow-Headers", config.getAccessControlAllowHeaders());
            response.getHeaders().set("Access-Control-Allow-Credentials", config.getAccessControlAllowCredentials());

            // OPTIONS 요청이면 빈 응답 반환 (403 방지)
            if (HttpMethod.OPTIONS.equals(request.getMethod())) {
                response.setStatusCode(org.springframework.http.HttpStatus.OK);
                return response.writeWith(Mono.empty());
            }

            // 요청 정보 로깅
            HttpMethod method = request.getMethod();
            String clientIpAddress = request.getRemoteAddress().getAddress().getHostAddress();
            log.info("Client IP Address: {}", clientIpAddress);
            log.info("Request Path: {}", request.getPath());
            log.info("Request Method: {}", method);

            return chain.filter(exchange);
        });
    }
}
