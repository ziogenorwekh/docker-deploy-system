package store.shportfolio.deploy.application.ports.input.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import store.shportfolio.deploy.application.exception.*;
import store.shportfolio.deploy.application.handler.DockerContainerHandler;
import store.shportfolio.deploy.application.handler.StorageHandler;
import store.shportfolio.deploy.application.handler.WebAppHandler;
import store.shportfolio.deploy.application.ports.input.DockerContainerizationUseCase;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

import java.io.File;

@Slf4j
@Component
public class DockerContainerizationUseCaseImpl implements DockerContainerizationUseCase {

    private final StorageHandler storageHandler;
    private final WebAppHandler webAppHandler;
    private final DockerContainerHandler dockerContainerHandler;

    public DockerContainerizationUseCaseImpl(StorageHandler storageHandler,
                                             WebAppHandler webAppHandler,
                                             DockerContainerHandler dockerContainerHandler) {
        this.storageHandler = storageHandler;
        this.webAppHandler = webAppHandler;
        this.dockerContainerHandler = dockerContainerHandler;
    }

    @Async
    @Override
    public void uploadWebAppFile(WebApp webApp, File file) {
        handleDeployment(webApp, file, false);
    }

    @Async
    @Override
    public void reUploadWebAppFile(WebApp webApp, File file) {
        handleDeployment(webApp, file, true);
    }

    private void handleDeployment(WebApp webApp, File file, boolean isReUpload) {
        Storage storage = null;
        log.info("Start {} webapp file asynchronously", isReUpload ? "re-deploying" : "deploying");

        try {
            if (isReUpload) {
                storageHandler.deleteStorage(webApp.getId().getValue());
            }

            storage = storageHandler.uploadS3(webApp.getId().getValue(), file);
            log.info("Storage saved data URL: {}, Filename: {}", storage.getStorageUrl().getValue(),
                    storage.getStorageName().getValue());

            containerization(webApp, storage.getStorageUrl());

        } catch (S3Exception | DockerContainerException |
                 ContainerAccessException | DockerContainerCreatingFailedException |
                 DockerContainerRunException | DockerImageCreationException e) {
            log.error("Deployment failed: {}", e.getMessage());
            webAppHandler.failedApplication(webApp, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during deployment: {}", e.getMessage());
            webAppHandler.failedApplication(webApp, "server error");
        } finally {
            deleteTempFile(file);
        }
    }

    private void containerization(WebApp webApp, StorageUrl storageUrl) {
        dockerContainerHandler.createDockerImageAndRun(webApp, storageUrl.getValue());
        log.info("Docker container status is {}", webApp.getApplicationStatus());

        // 성공 상태 업데이트
        webAppHandler.completeContainerizing(webApp);
        log.info("Docker container processed and application completed -> {}", webApp.getApplicationStatus());
    }

    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            if (file.delete()) {
                log.info("Temporary file deleted successfully: {}", file.getAbsolutePath());
            } else {
                log.warn("Failed to delete temporary file: {}", file.getAbsolutePath());
            }
        }
    }
}
