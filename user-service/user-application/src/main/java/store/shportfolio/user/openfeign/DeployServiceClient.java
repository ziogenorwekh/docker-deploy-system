package store.shportfolio.user.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "deploy-service",url = "${gateway.url}")
public interface DeployServiceClient {

    @RequestMapping(path = "/api/apps/{userId}", method = RequestMethod.DELETE,
            produces = "application/json")
    void deleteUserApplication(@PathVariable("userId") String userId);
}
