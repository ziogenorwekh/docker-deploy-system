package store.shportfolio.user.application.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "database-service")
public interface DatabaseServiceClient {

    @RequestMapping(path = "/api/databases/{userId}", method = RequestMethod.DELETE,
            produces = "application/json")
    ResponseEntity<Void> deleteUserDatabase(@PathVariable("userId") String userId);
}
