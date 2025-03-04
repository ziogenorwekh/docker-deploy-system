package store.shportfolio.deploy.application.api;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
                                                             @RequestHeader("X-Authenticated-Username") String username,
                                                             @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        WebAppCreateResponse webAppCreateResponse = deployApplicationService.createWebApp(userInfo, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(webAppCreateResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.POST)
    public ResponseEntity<Void> saveJarFile(@PathVariable UUID applicationId,
                                            @RequestPart(value = "file") MultipartFile file,
                                            @RequestHeader("X-Authenticated-Username") String username,
                                            @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        WebAppFileCreateCommand webAppFileCreateCommand = WebAppFileCreateCommand.builder()
                .applicationId(applicationId.toString()).file(file).build();
        deployApplicationService.saveJarFileAndCreateContainer(webAppFileCreateCommand, userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps/starting/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> startContainer(@PathVariable String applicationId, @RequestHeader("X-Authenticated-Username") String username,
                                               @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        deployApplicationService.startContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps/stopping/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> stopContainer(@PathVariable String applicationId, @RequestHeader("X-Authenticated-Username") String username,
                                              @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        deployApplicationService.stopContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/apps/logs/{applicationId}",
            method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppContainerResponse> retrieveTrackWebAppContainer(
            @PathVariable String applicationId,
            @RequestHeader("X-Authenticated-Username") String username,
            @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        WebAppContainerResponse webAppContainerResponse = deployApplicationService
                .trackQueryDockerContainerResponse(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppContainerResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> retrieveTrackWebApp(@PathVariable String applicationId,
                                                                   @RequestHeader("X-Authenticated-Username") String username,
                                                                   @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        WebAppTrackResponse webAppTrackResponse = deployApplicationService
                .trackQueryWebApp(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppTrackResponse);
    }

    @RequestMapping(path = "/apps/{applicationId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> deleteWebApp(@PathVariable String applicationId,
                                                            @RequestHeader("X-Authenticated-Username") String username,
                                                            @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        deployApplicationService
                .deleteWebApp(WebAppDeleteCommand.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/apps", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> deleteAllWebApps(@RequestHeader("X-Authenticated-Username") String username,
                                                 @RequestHeader("X-Authenticated-UserId") String userId) {
        UserGlobal userInfo = UserGlobal.builder().userId(userId).username(username).build();
        deployApplicationService.deleteAllWebApps(userInfo);
        return ResponseEntity.noContent().build();
    }


    private UserGlobal getUserGlobalByFeignClient(String token) {
        try {
            return userServiceClient.getUserInfo(token);
        } catch (FeignException ex) {
            throw new UserNotfoundException("FeignClient error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new UserNotfoundException("Unexpected error: " + ex.getMessage(), ex);
        }
    }
}
