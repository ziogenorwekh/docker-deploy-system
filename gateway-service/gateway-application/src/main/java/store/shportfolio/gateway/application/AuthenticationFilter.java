package store.shportfolio.gateway.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import store.shportfolio.gateway.application.config.AuthConfigData;

import java.util.List;

@Slf4j

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthConfigData> {

    public AuthenticationFilter() {
        super(AuthConfigData.class);
    }

    @Override
    public GatewayFilter apply(AuthConfigData config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Authorization 헤더 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing.", HttpStatus.UNAUTHORIZED);
            }

            List<String> authorizations = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authorizations == null || authorizations.isEmpty() || !authorizations.get(0).startsWith("Bearer ")) {
                return onError(exchange, "Invalid Authorization header.", HttpStatus.UNAUTHORIZED);
            }

            String token = authorizations.get(0).substring("Bearer ".length());

            try {
                String issuer = JWT.require(Algorithm.HMAC256(config.getSecret()))
                        .build()
                        .verify(token)
                        .getIssuer();

                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Authenticated-User", issuer)
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (JWTDecodeException | AlgorithmMismatchException | SignatureVerificationException
                     | TokenExpiredException e) {
                log.error("class.type is -> {}, Token error is -> {}", e.getClass(), e.getMessage());
                return onError(exchange, "Invalid or expired token.", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errMessage, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(errMessage); // 내부 로그에만 에러 메시지 기록
        response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, "Authentication failed."); // 외부 클라이언트용 메시지
        return response.setComplete();
    }
}