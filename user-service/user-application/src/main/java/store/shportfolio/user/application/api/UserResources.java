package store.shportfolio.user.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.application.openfeign.DatabaseServiceClient;
import store.shportfolio.user.application.openfeign.DeployServiceClient;

@RestController
@RequestMapping("/api")
public class UserResources {

    private final UserApplicationService userApplicationService;
    private final JwtHandler jwtHandler;
    private final DatabaseServiceClient databaseServiceClient;
    private final DeployServiceClient deployServiceClient;

    @Autowired
    public UserResources(UserApplicationService userApplicationService, JwtHandler jwtHandler,
                         DatabaseServiceClient databaseServiceClient,
                         DeployServiceClient deployServiceClient) {
        this.userApplicationService = userApplicationService;
        this.jwtHandler = jwtHandler;
        this.databaseServiceClient = databaseServiceClient;
        this.deployServiceClient = deployServiceClient;
    }


    @RequestMapping(path = "/users", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateCommand userCreateCommand
            , @RequestHeader(name = "Authorization") String token) {
        userCreateCommand.setToken(token);
        UserCreateResponse user = userApplicationService.createUser(userCreateCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<UserTrackResponse> retrieveUser(@PathVariable String userId,
                                                          @RequestHeader(name = "Authorization") String token) {
        String userIdFromToken = jwtHandler.getUserIdFromToken(userId, token);
        UserTrackResponse userTrackResponse = userApplicationService
                .trackQueryUser(UserTrackQuery.builder().userId(userIdFromToken).build());
        return ResponseEntity.status(HttpStatus.OK).body(userTrackResponse);
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable String userId,
                                           @RequestBody UserUpdateCommand userUpdateCommand,
                                           @RequestHeader("Authorization") String token) {
        String userIdFromToken = jwtHandler.getUserIdFromToken(userId, token);

        userApplicationService.updateUser(UserUpdateCommand.builder().userId(userIdFromToken)
                .currentPassword(userUpdateCommand.getCurrentPassword())
                .newPassword(userUpdateCommand.getNewPassword())
                .build());
        return ResponseEntity.noContent().build();
    }

    // 여기 수정해야 돼
    @RequestMapping(path = "/users/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        // 여기 수정해야 됌
        String userIdFromToken = jwtHandler.getUserIdFromToken(userId, token);
        UserDeleteEvent userDeleteEvent = userApplicationService.deleteUser(UserDeleteCommand
                .builder().userId(userIdFromToken).build());
        // feign client send userDeleteEvent business logic

        ResponseEntity<Void> deleteUserDatabase = databaseServiceClient
                .deleteUserDatabase(token);
        ResponseEntity<Void> deleteAllUserApplication = deployServiceClient.
                deleteAllUserApplication(token);
        if (deleteUserDatabase.getStatusCode() == HttpStatus.NO_CONTENT &&
                deleteAllUserApplication.getStatusCode() == HttpStatus.NO_CONTENT) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
