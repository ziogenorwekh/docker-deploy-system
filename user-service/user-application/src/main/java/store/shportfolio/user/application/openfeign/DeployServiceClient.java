package store.shportfolio.user.application.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "deploy-service",url = "${gateway.deploy-service.url}")
public interface DeployServiceClient {

    @RequestMapping(path = "/api/apps", method = RequestMethod.DELETE,
            produces = "application/json")
    ResponseEntity<Void> deleteAllUserApplication(@RequestHeader("Authorization") String token);
}
