package store.shportfolio.database.application.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.DatabaseUseCase;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

@Slf4j
@RestController
@RequestMapping(path = "/api")
public class DatabaseResources {

    private final DatabaseUseCase databaseUseCase;


    public DatabaseResources(DatabaseUseCase databaseUseCase) {
        this.databaseUseCase = databaseUseCase;
    }

    @RequestMapping(path = "/databases", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<DatabaseCreateResponse> createDatabases(
            @RequestBody DatabaseCreateCommand databaseCreateCommand,
            @RequestHeader("X-Authenticated-Username") String username,
            @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();

        DatabaseCreateResponse databaseCreateResponse = databaseUseCase
                .createDatabase(databaseCreateCommand, userInfo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(databaseCreateResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<DatabaseTrackResponse> retrieveDatabase(@RequestHeader("X-Authenticated-Username") String username,
                                                                   @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        DatabaseTrackQuery databaseTrackQuery = DatabaseTrackQuery.builder().userId(userInfo.getUserId()).build();
        DatabaseTrackResponse databaseTrackResponse = databaseUseCase.trackQuery(databaseTrackQuery);
        return ResponseEntity.status(HttpStatus.OK).body(databaseTrackResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        databaseUseCase.deleteDatabase(userInfo);
        return ResponseEntity.noContent().build();
    }

}
