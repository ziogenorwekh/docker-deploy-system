package store.shportfolio.user.application.api;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.user.application.exception.UserNotOwnerException;
import store.shportfolio.user.usecase.ports.input.UserUseCase;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.exception.CustomServiceUnavailableException;
import store.shportfolio.user.application.openfeign.DatabaseServiceClient;
import store.shportfolio.user.application.openfeign.DeployServiceClient;


@Slf4j
@RestController
@RequestMapping("/api")
public class UserResources {

    private final UserUseCase userUseCase;
    private final DeployServiceClient deployServiceClient;
    private final DatabaseServiceClient databaseServiceClient;
    @Autowired
    public UserResources(UserUseCase userUseCase,
                         DeployServiceClient deployServiceClient,
                         DatabaseServiceClient databaseServiceClient
    )
    {
        this.userUseCase = userUseCase;
        this.deployServiceClient = deployServiceClient;
        this.databaseServiceClient = databaseServiceClient;
    }


    @RequestMapping(path = "/users", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateCommand userCreateCommand
            , @RequestHeader(name = "Authorization") String token) {
        userCreateCommand.setToken(token);
        UserCreateResponse user = userUseCase.createUser(userCreateCommand);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<UserTrackResponse> retrieveUser(@PathVariable String userId,
                                                          @RequestHeader(name = "X-Authenticated-UserId") String userIdFromToken) {
        validateEqualsRequesterAndBearer(userId, userIdFromToken);
        UserTrackResponse userTrackResponse = userUseCase
                .trackQueryUser(UserTrackQuery.builder().userId(userIdFromToken).build());
        return ResponseEntity.status(HttpStatus.OK).body(userTrackResponse);
    }

    @RequestMapping(path = "/users/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateUser(@PathVariable String userId,
                                           @RequestBody UserUpdateCommand userUpdateCommand,
                                           @RequestHeader(name = "X-Authenticated-UserId") String userIdFromToken) {
        validateEqualsRequesterAndBearer(userId, userIdFromToken);
        userUpdateCommand.setUserId(userIdFromToken);
        userUseCase.updateUser(userUpdateCommand);
        return ResponseEntity.noContent().build();
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackDeleteUser")
    @RequestMapping(path = "/users/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable String userId,
                                           @RequestHeader(name = "X-Authenticated-UserId") String userIdFromToken,
                                           @RequestHeader(name = "X-Authenticated-Username") String username,
                                           @RequestHeader(name = "Authorization") String token) {
        validateEqualsRequesterAndBearer(userId, userIdFromToken);
        try {
            databaseServiceClient.deleteUserDatabase(userIdFromToken,username,token);
        } catch (FeignException fe) {
            log.error("error message: {}", fe.getMessage());
            log.error("Deploy service unavailable");
            throw new CustomServiceUnavailableException("Database service unavailable");
        }

        try {
            deployServiceClient.deleteAllUserApplication(userIdFromToken,username,token);
        } catch (FeignException fe) {
            log.error("error message: {}", fe.getMessage());
            log.error("Deploy service unavailable");
            throw new CustomServiceUnavailableException("Deploy service unavailable");
        }

        userUseCase.deleteUser(UserDeleteCommand.builder().userId(userIdFromToken).build());
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> fallbackDeleteUser(String userId, String token, Throwable t) {
        log.warn("User deletion failed for userId: {} due to: {}", userId, t.getMessage(), t);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("X-Fallback-Reason", "User deletion failed due to service unavailability.")
                .build();
    }


    private void validateEqualsRequesterAndBearer(String userId, String tokenFromUserId) {
        if (!userId.equals(tokenFromUserId)) {
            throw new UserNotOwnerException(String.format("User with id %s does not match token %s",
                    userId, tokenFromUserId));
        }
    }
}
