package store.shportfolio.user.application.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "database-service",url = "${gateway.database-service.url}")
public interface DatabaseServiceClient {

    @RequestMapping(path = "/api/databases/{userId}", method = RequestMethod.DELETE,
            produces = "application/json")
    ResponseEntity<Void> deleteUserDatabase(@RequestHeader("Authorization") String token);
}
