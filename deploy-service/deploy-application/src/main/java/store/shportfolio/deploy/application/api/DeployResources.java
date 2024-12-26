package store.shportfolio.deploy.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.DeployApplicationService;
import store.shportfolio.deploy.application.command.*;
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

    @RequestMapping(path = "/deployments", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<WebAppCreateResponse> createWebApp(@RequestBody WebAppCreateCommand command,
                                                             @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        WebAppCreateResponse webAppCreateResponse = deployApplicationService.createWebApp(userInfo, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(webAppCreateResponse);
    }

    @RequestMapping(path = "/deployments/{applicationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Void> saveJarFile(@PathVariable UUID applicationId,
                                            @RequestBody WebAppFileCreateCommand webAppFileCreateCommand,
                                            @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        webAppFileCreateCommand.setApplicationId(applicationId.toString());

        deployApplicationService.saveJarFile(webAppFileCreateCommand, userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/deployments/starting/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> startContainer(@PathVariable String applicationId, @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        deployApplicationService.startContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/deployments/stopping/{applicationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> stopContainer(@PathVariable String applicationId, @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        deployApplicationService.stopContainer(WebAppTrackQuery.builder()
                .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/deployments/container/{applicationId}",
            method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppContainerResponse> retrieveTrackWebAppContainer(
            @PathVariable String applicationId,
            @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        WebAppContainerResponse webAppContainerResponse = deployApplicationService
                .trackQueryDockerContainerResponse(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppContainerResponse);
    }

    @RequestMapping(path = "/deployments/{applicationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> retrieveTrackWebApp(@PathVariable String applicationId,
                                                                   @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        WebAppTrackResponse webAppTrackResponse = deployApplicationService
                .trackQueryWebApp(WebAppTrackQuery.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(webAppTrackResponse);
    }

    @RequestMapping(path = "/deployments/{applicationId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<WebAppTrackResponse> deleteWebApp(@PathVariable String applicationId,
                                                                   @RequestHeader("Authorization") String token) {
        UserGlobal userInfo = userServiceClient.getUserInfo(token);
        deployApplicationService
                .deleteWebApp(WebAppDeleteCommand.builder()
                        .applicationId(UUID.fromString(applicationId)).build(), userInfo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
