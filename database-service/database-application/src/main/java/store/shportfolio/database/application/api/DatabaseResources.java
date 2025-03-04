package store.shportfolio.database.application.api;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.DatabaseApplicationService;
import store.shportfolio.database.application.exception.UserNotfoundException;
import store.shportfolio.database.application.openfeign.UserServiceClient;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackQuery;
import store.shportfolio.database.application.command.DatabaseTrackResponse;

@Slf4j
@RestController
@RequestMapping(path = "/api")
public class DatabaseResources {

    private final UserServiceClient userServiceClient;
    private final DatabaseApplicationService databaseApplicationService;


    public DatabaseResources(UserServiceClient userServiceClient, DatabaseApplicationService databaseApplicationService) {
        this.userServiceClient = userServiceClient;
        this.databaseApplicationService = databaseApplicationService;
    }

    @RequestMapping(path = "/databases", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<DatabaseCreateResponse> createDatabases(
            @RequestBody DatabaseCreateCommand databaseCreateCommand,
            @RequestHeader("X-Authenticated-Username") String username,
            @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();

        DatabaseCreateResponse databaseCreateResponse = databaseApplicationService
                .createDatabase(databaseCreateCommand, userInfo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseCreateResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<DatabaseTrackResponse> retrieveDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                                   @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        DatabaseTrackQuery databaseTrackQuery = DatabaseTrackQuery.builder().userId(userInfo.getUserId()).build();
        DatabaseTrackResponse databaseTrackResponse = databaseApplicationService.trackQuery(databaseTrackQuery);
        return ResponseEntity.status(HttpStatus.OK).body(databaseTrackResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        databaseApplicationService.deleteDatabase(userInfo);
        return ResponseEntity.noContent().build();
    }


    private UserGlobal getUserGlobalByFeignClient(String token) {
        try {
            ResponseEntity<UserGlobal> userGlobalResponseEntity = userServiceClient.getUserInfo(token);
            if (userGlobalResponseEntity.getStatusCode().is2xxSuccessful() && userGlobalResponseEntity.getBody() != null) {
                return userGlobalResponseEntity.getBody();
            } else {
                throw new UserNotfoundException("User not found");
            }
        } catch (FeignException ex) {
            throw new UserNotfoundException("FeignClient error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new UserNotfoundException("Unexpected error: " + ex.getMessage(), ex);
        }
    }
}
