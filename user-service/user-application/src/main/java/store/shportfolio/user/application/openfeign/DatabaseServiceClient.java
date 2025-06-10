package store.shportfolio.user.application.openfeign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "database-service", url = "${gateway.database-service.url}")
public interface DatabaseServiceClient {

    @Retry(name = "database-service", fallbackMethod = "fallbackDeleteUserDatabase")
    @CircuitBreaker(name = "database-service", fallbackMethod = "fallbackDeleteUserDatabase")
    @RequestMapping(path = "/api/databases", method = RequestMethod.DELETE,
            produces = "application/json")
    ResponseEntity<Void> deleteUserDatabase(@RequestHeader("Authorization") String token);

    default ResponseEntity<Void> fallbackDeleteUserDatabase(String token, Throwable t) {
        System.err.println("Database deletion failed: " + t.getMessage());
        return ResponseEntity.status(503)
                .header("X-Fallback-Reason", "Database deletion service unavailable.")
                .build();
    }
}
