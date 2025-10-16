package store.shportfolio.deploy.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.*;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.application.exception.*;
import store.shportfolio.deploy.application.handler.DockerContainerHandler;
import store.shportfolio.deploy.application.handler.StorageHandler;
import store.shportfolio.deploy.application.handler.WebAppHandler;
import store.shportfolio.deploy.application.mapper.DeployDataMapper;
import store.shportfolio.deploy.application.ports.input.DockerContainerizationUseCase;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Validated
public class DeployApplicationServiceImpl implements DeployApplicationService {

    private final DockerContainerHandler dockerContainerHandler;
    private final StorageHandler storageHandler;
    private final WebAppHandler webAppHandler;
    private final DeployDataMapper deployDataMapper;
    private final DockerContainerizationUseCase dockerContainerizationUseCase;
    @Autowired
    public DeployApplicationServiceImpl(DockerContainerHandler dockerContainerHandler,
                                        StorageHandler storageHandler, WebAppHandler webAppHandler,
                                        DeployDataMapper deployDataMapper,
                                        DockerContainerizationUseCase dockerContainerizationUseCase) {
        this.dockerContainerHandler = dockerContainerHandler;
        this.storageHandler = storageHandler;
        this.webAppHandler = webAppHandler;
        this.deployDataMapper = deployDataMapper;
        this.dockerContainerizationUseCase = dockerContainerizationUseCase;
    }

    @Override
    @Transactional
    public WebAppCreateResponse createWebApp(UserGlobal userGlobal, WebAppCreateCommand webAppCreateCommand) {

        webAppHandler.isExistApplicationName(webAppCreateCommand.getApplicationName());
        webAppHandler.isExistPort(webAppCreateCommand.getPort());
        WebApp webApp = webAppHandler.createWebApp(userGlobal, webAppCreateCommand);

        Storage storage = storageHandler.createStorage(webApp);
        DockerContainer dockerContainer = dockerContainerHandler
                .createDockerContainer(webApp);


        WebApp saved = webAppHandler.saveWebApp(webApp);
        storageHandler.saveStorage(storage);
        dockerContainerHandler.saveDockerContainer(dockerContainer);
        log.info("WebApp created: {}", saved.getApplicationName().getValue());

        return deployDataMapper.webAppToWebAppCreateResponse(saved);
    }


    @Override
    public void saveJarFile(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal) {
        WebApp webApp = validateAndRetrieveWebApp(webAppFileCreateCommand, userGlobal);
        webAppHandler.startContainerizing(webApp);
        log.info("Containerizing started -> {}", webApp.getApplicationStatus());
        File file;
        try {
            file =  this.convertMultipartFileToFile(webAppFileCreateCommand.getFile());
        } catch (IOException e) {
            throw new FileExchangeFailedException("File conversion failed.");
        }
        dockerContainerizationUseCase.uploadWebAppFile(webApp, file);
    }

    @Override
    public void reDeployJarFile(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal) {
        WebApp webApp = this.getWebApp(userGlobal,UUID.fromString(webAppFileCreateCommand.getApplicationId()));
        webAppHandler.reDeployApplication(webApp);
        webAppHandler.startContainerizing(webApp);
        log.info("Containerizing started -> {}", webApp.getApplicationStatus());
        File file;
        try {
            file =  this.convertMultipartFileToFile(webAppFileCreateCommand.getFile());
        } catch (IOException e) {
            throw new FileExchangeFailedException("File conversion failed.");
        }
        dockerContainerizationUseCase.reUploadWebAppFile(webApp, file);
    }



    @Override
    @Transactional(readOnly = true)
    public WebAppTrackResponse trackQueryWebApp(WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal) {
        UUID applicationId = webAppTrackQuery.getApplicationId();
        log.info("query webApp id is {}", applicationId);
        WebApp webApp = this.getWebApp(userGlobal, applicationId);
        DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(applicationId);

        return deployDataMapper.webAppToWebAppTrackResponse(webApp, dockerContainer.getEndPointUrl()
                , dockerContainer.getDockerContainerStatus());
    }

    @Override
    public List<WebAppTrackResponse> trackQueryAllWebApps(UserGlobal userGlobal) {
        List<WebApp> webApps = webAppHandler.findAll(userGlobal.getUserId());
        Stream<WebAppTrackResponse> webAppTrackResponseStream = webApps.stream().map(webApp -> {
            DockerContainer dockerContainer = dockerContainerHandler.getDockerContainer(webApp.getId().getValue());
            return deployDataMapper.webAppToWebAppTrackResponse(webApp, dockerContainer.getEndPointUrl(), dockerContainer.getDockerContainerStatus());
        });
        return webAppTrackResponseStream.collect(Collectors.toList());
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

        return deployDataMapper.webAppToWebAppContainerResponse(webApp, resourceUsage, containerLogs,
                dockerContainer.getDockerContainerStatus());
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
        log.info("webApp id is {}", webApp.getId().getValue());
        if (!webApp.getUserId().getValue().equals(userGlobal.getUserId())) {
            log.error("user global id is different");
            throw new WebAppUserNotMatchException(String.format("Web app %s does not match user %s",
                    webApp.getApplicationName().getValue(), userGlobal.getUserId()));
        }
        return webApp;
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String prefix = originalFilename != null ? originalFilename.substring(0, originalFilename.lastIndexOf('.')) : "temp";
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".tmp";
        File tempFile = File.createTempFile(prefix + "-", suffix);
        multipartFile.transferTo(tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }
}
