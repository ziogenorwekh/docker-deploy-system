package store.shportfolio.user.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.application.openfeign.DatabaseServiceClient;
import store.shportfolio.user.application.openfeign.DeployServiceClient;

@RestController
@RequestMapping("/api")
public class UserResources {

    private final UserApplicationService userApplicationService;
    private final DatabaseServiceClient databaseServiceClient;
    private final DeployServiceClient deployServiceClient;
    @Autowired
    public UserResources(UserApplicationService userApplicationService,
                         DatabaseServiceClient databaseServiceClient,
                         DeployServiceClient deployServiceClient) {
        this.userApplicationService = userApplicationService;
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
    public ResponseEntity<UserTrackResponse> retrieveUser(@PathVariable String userId) {
        UserTrackResponse userTrackResponse = userApplicationService
                .trackQueryUser(UserTrackQuery.builder().userId(userId).build());
        return ResponseEntity.status(HttpStatus.OK).body(userTrackResponse);
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable String userId,
                                           @RequestBody UserUpdateCommand userUpdateCommand) {
        userApplicationService.updateUser(UserUpdateCommand.builder().userId(userId)
                .currentPassword(userUpdateCommand.getCurrentPassword())
                .newPassword(userUpdateCommand.getNewPassword())
                .build());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        UserDeleteEvent userDeleteEvent = userApplicationService.deleteUser(UserDeleteCommand.builder().userId(userId).build());
        // feign client send userDeleteEvent business logic
        databaseServiceClient.deleteUserDatabase(userDeleteEvent.getEntity().getId().getValue());
        deployServiceClient.deleteUserApplication(userDeleteEvent.getEntity().getId().getValue());
        //
        return ResponseEntity.noContent().build();
    }
}
