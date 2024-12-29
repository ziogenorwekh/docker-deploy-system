package store.shportfolio.database.application.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import store.shportfolio.common.domain.valueobject.UserGlobal;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @RequestMapping(path = "/api/auth/user/info", method = RequestMethod.GET,
            produces = "application/json")
    ResponseEntity<UserGlobal> getUserInfo(@RequestHeader("Authorization") String token);
}
