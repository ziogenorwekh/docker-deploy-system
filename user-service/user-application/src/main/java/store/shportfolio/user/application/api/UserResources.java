package store.shportfolio.user.application.api;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.UserDeleteException;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.application.openfeign.DatabaseServiceClient;
import store.shportfolio.user.application.openfeign.DeployServiceClient;


@Slf4j
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

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackDeleteUser")
    @RequestMapping(path = "/users/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        String userIdFromToken = jwtHandler.getUserIdFromToken(userId, token);
        userApplicationService.deleteUser(UserDeleteCommand.builder().userId(userIdFromToken).build());

        // 각각의 외부 서비스 호출에도 서킷 브레이커 적용 필요
        ResponseEntity<Void> deleteUserDatabase = databaseServiceClient.deleteUserDatabase(token);
        ResponseEntity<Void> deleteAllUserApplication = deployServiceClient.deleteAllUserApplication(token);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> fallbackDeleteUser(String userId, String token, Throwable t) {
        log.warn("User deletion process failed for userId: {} due to: {}", userId, t.getMessage(), t);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("X-Fallback-Reason", "User deletion process failed due to service unavailability.")
                .build();
    }
}
