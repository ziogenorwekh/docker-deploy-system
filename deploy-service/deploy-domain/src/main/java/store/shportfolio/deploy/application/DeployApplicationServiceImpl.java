package store.shportfolio.deploy.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.*;
import store.shportfolio.deploy.application.exception.*;
import store.shportfolio.deploy.application.handler.DockerContainerHandler;
import store.shportfolio.deploy.application.handler.StorageHandler;
import store.shportfolio.deploy.application.handler.WebAppHandler;
import store.shportfolio.deploy.application.mapper.DeployDataMapper;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Validated
public class DeployApplicationServiceImpl implements DeployApplicationService {

    private final DockerContainerHandler dockerContainerHandler;
    private final StorageHandler storageHandler;
    private final WebAppHandler webAppHandler;
    private final DeployDataMapper deployDataMapper;

    @Autowired
    public DeployApplicationServiceImpl(DockerContainerHandler dockerContainerHandler,
                                        StorageHandler storageHandler, WebAppHandler webAppHandler, DeployDataMapper deployDataMapper) {
        this.dockerContainerHandler = dockerContainerHandler;
        this.storageHandler = storageHandler;
        this.webAppHandler = webAppHandler;
        this.deployDataMapper = deployDataMapper;
    }

    @Override
    @Transactional
    public WebAppCreateResponse createWebApp(UserGlobal userGlobal, WebAppCreateCommand webAppCreateCommand) {

        webAppHandler.isExistApplicationName(webAppCreateCommand.getApplicationName());

        WebApp webApp = webAppHandler.createWebApp(userGlobal, webAppCreateCommand);

        Storage storage = storageHandler.createStorage(webApp);
        DockerContainer dockerContainer = dockerContainerHandler
                .createDockerContainer(webApp);


        WebApp saved = webAppHandler.saveWebApp(webApp);
        storageHandler.saveStorage(storage);
        dockerContainerHandler.saveDockerContainer(dockerContainer);
        log.info("WebApp created: {}", saved.getApplicationName());

        return deployDataMapper.webAppToWebAppCreateResponse(saved);
    }


    @Override
    public void saveJarFileAndCreateContainer(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal) {
        WebApp webApp = validateAndRetrieveWebApp(webAppFileCreateCommand, userGlobal);

        try {
            Storage storage = storageHandler.uploadS3(webApp.getId().getValue(), webAppFileCreateCommand.getFile());
            log.info("Storage saved data URL: {}, Filename: {}", storage.getStorageUrl().getValue(),
                    storage.getStorageName().getValue());

            webAppHandler.startContainerizing(webApp);
            log.info("Containerizing started -> {}", webApp.getApplicationStatus());

            // CompletableFuture 호출
            processDockerContainerAndComplete(webApp, storage.getStorageUrl());

        } catch (IOException | S3Exception e) {
            log.error("Error during storage processing: {}", e.getMessage());
            webAppHandler.failedApplication(webApp, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            webAppHandler.failedApplication(webApp, "server error");
        }
    }


    @Override
    @Transactional(readOnly = true)
    public WebAppTrackResponse trackQueryWebApp(WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal) {
        UUID applicationId = webAppTrackQuery.getApplicationId();
        log.info("query webApp id is {}", applicationId);
        WebApp webApp = this.getWebApp(userGlobal, applicationId);
        DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(applicationId);

        return deployDataMapper.webAppToWebAppTrackResponse(webApp,dockerContainer.getEndPointUrl());
    }

    @Override
    @Transactional
    public void startContainer(WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal) {
        UUID applicationId = webAppTrackQuery.getApplicationId();
        webAppHandler.isMatchUser(userGlobal.getUserId(), applicationId);
        DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(applicationId);
        dockerContainerHandler.startContainer(dockerContainer);
    }

    @Override
    @Transactional
    public void stopContainer(WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal) {
        UUID applicationId = webAppTrackQuery.getApplicationId();
        webAppHandler.isMatchUser(userGlobal.getUserId(), applicationId);
        DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(applicationId);
        dockerContainerHandler.stopContainer(dockerContainer);
    }

    @Override
    @Transactional
    public void deleteWebApp(WebAppDeleteCommand webAppDeleteCommand, UserGlobal userGlobal) {
        UUID applicationId = webAppDeleteCommand.getApplicationId();
        log.info("delete webApp id is {}", applicationId);
        WebApp webApp = this.getWebApp(userGlobal, applicationId);

        webAppHandler.deleteWebApp(webApp);
        storageHandler.deleteStorage(webApp.getId().getValue());
        dockerContainerHandler.deleteDockerContainer(webApp.getId().getValue());
    }

    @Override
    @Transactional
    public void deleteAllWebApps(UserGlobal userGlobal) {
        List<WebApp> webApps = webAppHandler.findAll(userGlobal.getUserId());
        for (WebApp webApp : webApps) {
            WebAppDeleteCommand webAppDeleteCommand = WebAppDeleteCommand.builder()
                    .applicationId(webApp.getId().getValue()).build();
            this.deleteWebApp(webAppDeleteCommand, userGlobal);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WebAppContainerResponse trackQueryDockerContainerResponse(WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal) {
        UUID applicationId = webAppTrackQuery.getApplicationId();
        log.info("query webApp id is {}", applicationId);
        WebApp webApp = this.getWebApp(userGlobal, applicationId);
        DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(applicationId);
        ResourceUsage resourceUsage = dockerContainerHandler.getContainerUsage(dockerContainer);
        String containerLogs = dockerContainerHandler.getContainerLogs(dockerContainer);
        return deployDataMapper.webAppToWebAppContainerResponse(webApp, resourceUsage, containerLogs);
    }

    private synchronized CompletableFuture<Void> processDockerContainerAndComplete(WebApp webApp, StorageUrl storageUrl) {
        return CompletableFuture.runAsync(() -> {
            try {
                dockerContainerHandler.createDockerImageAndRun(webApp, storageUrl.getValue());
                webAppHandler.completeContainerizing(webApp);
                log.info("Docker container processed and application completed -> {}", webApp.getApplicationStatus());
            } catch (DockerContainerException | ContainerAccessException e) {
                log.error("Error while processing Docker container: {}", e.getMessage());
                webAppHandler.failedApplication(webApp, e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error while processing Docker container: {}", e.getMessage());
                webAppHandler.failedApplication(webApp, "server error");
            }
        });
    }

    private WebApp validateAndRetrieveWebApp(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal) {
        UUID applicationId = UUID.fromString(webAppFileCreateCommand.getApplicationId());
        WebApp webApp = this.getWebApp(userGlobal, applicationId);

        if (webApp.getApplicationStatus() != ApplicationStatus.CREATED
                && webApp.getApplicationStatus() != ApplicationStatus.FAILED) {
            throw new WebAppException("Application already operated.");
        }

        return webApp;
    }

    private WebApp getWebApp(UserGlobal userGlobal, UUID applicationId) {
        WebApp webApp = webAppHandler.getWebApp(applicationId);
        log.info("webApp id is {}", webApp.getId());
        if (!webApp.getUserId().getValue().equals(userGlobal.getUserId())) {
            log.error("user global id is different");
            throw new WebAppUserNotMatchException(String.format("Web app %s does not match user %s",
                    webApp.getApplicationName().getValue(), userGlobal.getUserId()));
        }
        return webApp;
    }

}
