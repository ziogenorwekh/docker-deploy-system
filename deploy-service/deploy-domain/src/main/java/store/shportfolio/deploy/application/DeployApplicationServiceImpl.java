package store.shportfolio.deploy.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.*;
import store.shportfolio.deploy.application.exception.ContainerAccessException;
import store.shportfolio.deploy.application.exception.DockerContainerException;
import store.shportfolio.deploy.application.exception.S3UploadFailedException;
import store.shportfolio.deploy.application.exception.WebAppUserNotMatchException;
import store.shportfolio.deploy.application.handler.DockerContainerHandler;
import store.shportfolio.deploy.application.handler.StorageHandler;
import store.shportfolio.deploy.application.handler.WebAppHandler;
import store.shportfolio.deploy.application.mapper.DeployDataMapper;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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


    @Async
    @Override
    @Transactional
    public void saveJarFile(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal) {
        UUID applicationId = UUID.fromString(webAppFileCreateCommand.getApplicationId());
        WebApp webApp = this.getWebApp(userGlobal, applicationId);
        try {
            Storage storage = storageHandler.uploadS3(webApp.getId().getValue(), webAppFileCreateCommand.getFile());
            log.info("storage saved data url is {}, filename is {}", storage.getStorageUrl(), storage.getStorageName());

            webAppHandler.startContainerizing(webApp);
            log.info("containerizing started -> {}", webApp.getApplicationStatus());

            DockerContainer dockerContainer = dockerContainerHandler.createDockerImageAndRun(webApp,
                    storage.getStorageUrl());
            log.info("docker container created Id -> {}", dockerContainer.getDockerContainerId().getValue());
            log.info("docker container endpoint is {}, container status is {}", dockerContainer.getEndPointUrl()
                    , dockerContainer.getDockerContainerStatus());

            webAppHandler.completeContainerizing(webApp);

            log.info("containerizing completed -> {}", webApp.getApplicationStatus());
            log.info("finally webApp must be updated DockerContainer is Id -> {}, is Endpoint -> {}. " +
                            "storage is fileUrl {}, is name -> {}", dockerContainer.getDockerContainerId().getValue(),
                    dockerContainer.getEndPointUrl(), storage.getStorageUrl(), storage.getStorageName());
        } catch (IOException | DockerContainerException e) {
            webAppHandler.failedApplication(webApp, e.getMessage());
            throw new S3UploadFailedException(e.getMessage());
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

    private WebApp getWebApp(UserGlobal userGlobal, UUID applicationId) {
        WebApp webApp = webAppHandler.getWebApp(applicationId);
        if (!webApp.getUserId().getValue().equals(userGlobal.getUserId())) {
            throw new WebAppUserNotMatchException(String.format("Web app %s does not match user %s",
                    webApp.getApplicationName(), userGlobal.getUserId()));
        }
        return webApp;
    }

}
