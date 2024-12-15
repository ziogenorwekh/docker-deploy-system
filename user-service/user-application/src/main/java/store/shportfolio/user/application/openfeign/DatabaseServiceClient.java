package store.shportfolio.user.application.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "database-service", url = "${gateway.url}")
public interface DatabaseServiceClient {

    @RequestMapping(path = "/api/databases/{userId}", method = RequestMethod.DELETE,
            produces = "application/json")
    void deleteUserDatabase(@PathVariable("userId") String userId);
}
