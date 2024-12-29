package store.shportfolio.deploy.application.api;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.DeployApplicationService;
import store.shportfolio.deploy.application.command.*;
import store.shportfolio.deploy.application.exception.UserNotfoundException;
import store.shportfolio.deploy.application.openfeign.UserServiceClient;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DeployResources {

    private final DeployApplicationService deployApplicationService;
    private final UserServiceClient userServiceClient;

    @Autowired
    public DeployResources(DeployApplicationService deployApplicationService,
                           UserServiceClient userServiceClient) {
        this.deployApplicationService = deployApplicationService;
        this.userServiceClient = userServiceClient;
    }

    @RequestMapping(path = "/apps", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<WebAppCreateResponse> createWebApp(@RequestBody WebAppCreateCommand command,
                                                             @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        WebAppCreateResponse webAppCreateResponse = deployApplicationService.createWebApp(userInfo, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(webAppCreateResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Void> saveJarFile(@PathVariable UUID applicationId,
                                            @RequestBody WebAppFileCreateCommand webAppFileCreateCommand,
                                            @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        webAppFileCreateCommand.setApplicationId(applicationId.toString());

        deployApplicationService.saveJarFile(webAppFileCreateCommand, userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps/starting/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> startContainer(@PathVariable String applicationId, @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        deployApplicationService.startContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps/stopping/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> stopContainer(@PathVariable String applicationId, @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        deployApplicationService.stopContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps/container/{applicationId}",
            method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppContainerResponse> retrieveTrackWebAppContainer(
            @PathVariable String applicationId,
            @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        WebAppContainerResponse webAppContainerResponse = deployApplicationService
                .trackQueryDockerContainerResponse(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppContainerResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> retrieveTrackWebApp(@PathVariable String applicationId,
                                                                   @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        WebAppTrackResponse webAppTrackResponse = deployApplicationService
                .trackQueryWebApp(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppTrackResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> deleteWebApp(@PathVariable String applicationId,
                                                                   @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        deployApplicationService
                .deleteWebApp(WebAppDeleteCommand.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteAllWebApps(@RequestHeader("Authorization") String token) {
        UserGlobal userInfo = getUserGlobalByFeignClient(token);
        deployApplicationService.deleteAllWebApps(userInfo);
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
