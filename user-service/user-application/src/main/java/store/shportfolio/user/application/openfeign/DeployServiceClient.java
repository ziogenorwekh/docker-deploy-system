package store.shportfolio.user.application.openfeign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "deploy-service",url = "${gateway.deploy-service.url}")
public interface DeployServiceClient {

    @Retry(name = "deploy-service", fallbackMethod = "fallbackDeleteAllUserApplication")
    @CircuitBreaker(name = "deploy-service", fallbackMethod = "fallbackDeleteAllUserApplication")
    @RequestMapping(path = "/api/apps", method = RequestMethod.DELETE,
            produces = "application/json")
    ResponseEntity<Void> deleteAllUserApplication(@RequestHeader("Authorization") String token);

    default ResponseEntity<Void> fallbackDeleteAllUserApplication(String token, Throwable t) {
        System.err.println("User application deletion failed: " + t.getMessage());
        return ResponseEntity.status(503)
                .header("X-Fallback-Reason", "User application deletion service unavailable.")
                .build();
    }
}
