package store.shportfolio.database.application.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.DatabaseApplicationService;
import store.shportfolio.database.application.openfeign.UserServiceClient;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackQuery;
import store.shportfolio.database.application.command.DatabaseTrackResponse;

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
            @RequestBody DatabaseCreateCommand databaseCreateCommand, @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        DatabaseCreateResponse databaseCreateResponse = databaseApplicationService
                .createDatabase(databaseCreateCommand, userInfo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseCreateResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<DatabaseTrackResponse> retrieveDatabases(@RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        DatabaseTrackQuery databaseTrackQuery = DatabaseTrackQuery.builder().userId(userInfo.getUserId()).build();
        DatabaseTrackResponse databaseTrackResponse = databaseApplicationService.trackQuery(databaseTrackQuery);
        return ResponseEntity.status(HttpStatus.OK).body(databaseTrackResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteDatabases(@RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        databaseApplicationService.deleteDatabase(userInfo);
        return ResponseEntity.noContent().build();
    }

}
