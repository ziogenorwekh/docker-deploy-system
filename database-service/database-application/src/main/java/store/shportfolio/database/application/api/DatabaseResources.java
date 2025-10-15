package store.shportfolio.database.application.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.DatabaseUseCase;
import store.shportfolio.database.usecase.command.*;

import java.util.List;

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

    @RequestMapping(path = "/databases/{databaseName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<DatabaseTrackResponse> retrieveDatabase(@RequestHeader("X-Authenticated-Username") String username,
                                                                  @RequestHeader("X-Authenticated-UserId") String userId,
                                                                  @PathVariable String databaseName) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        DatabaseOneTrackQuery databaseOneTrackQuery = DatabaseOneTrackQuery.builder().userId(userInfo.getUserId())
                .databaseName(databaseName).build();
        DatabaseTrackResponse databaseTrackResponse = databaseUseCase.trackDatabase(databaseOneTrackQuery);
        return ResponseEntity.status(HttpStatus.OK).body(databaseTrackResponse);
    }

    @RequestMapping(path = "/databases", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<DatabaseTrackResponse>> retrieveDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                                         @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        DatabaseAllTrackQuery query = DatabaseAllTrackQuery.builder().userId(userInfo.getUserId())
                .build();
        List<DatabaseTrackResponse> databaseTrackResponses = databaseUseCase.trackDatabases(query);
        return ResponseEntity.status(HttpStatus.OK).body(databaseTrackResponses);
    }

    @RequestMapping(path = "/databases/{databaseName}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                @RequestHeader("X-Authenticated-UserId") String userId,
                                                @PathVariable String databaseName) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        DatabaseDeleteCommand build = DatabaseDeleteCommand.builder()
                .databaseName(databaseName).build();
        databaseUseCase.deleteDatabase(userInfo, build);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/databases",method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteAllDatabases(@RequestHeader("X-Authenticated-Username") String username,
                                                @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        databaseUseCase.deleteAllDatabases(userInfo);
        return ResponseEntity.noContent().build();
    }

}
