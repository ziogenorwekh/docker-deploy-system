package store.shportfolio.deploy.infrastructure.jpa.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.*;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerId;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;

import java.util.UUID;

@Component
public class DeployDataAccessMapper {


    public DockerContainerEntity dockerContainerToDockerContainerEntity(DockerContainer dockerContainer) {
        return DockerContainerEntity
                .builder()
                .webAppEntity(WebAppEntity.builder().applicationId(dockerContainer.getId()
                        .getValue().toString()).build())
                .dockerContainerId(dockerContainer.getDockerContainerId().getValue())
                .dockerContainerStatus(dockerContainer.getDockerContainerStatus())
                .applicationId(dockerContainer.getId().getValue().toString())
                .endPointUrl(dockerContainer.getEndPointUrl())
                .build();
    }

    public DockerContainer dockerContainerEntityToDockerContainer(DockerContainerEntity dockerContainerEntity) {
        return DockerContainer.builder()
                .dockerContainerStatus(dockerContainerEntity.getDockerContainerStatus())
                .applicationId(new ApplicationId(UUID.fromString(dockerContainerEntity.getApplicationId())))
                .endPointUrl(dockerContainerEntity.getEndPointUrl())
                .dockerContainerId(new DockerContainerId(dockerContainerEntity.getDockerContainerId()))
                .build();

    }

    public StorageEntity storageEntityToStorage(Storage storage) {
        return StorageEntity.builder()
                .applicationId(storage.getId().getValue().toString())
                .storageName(storage.getStorageName())
                .storageUrl(storage.getStorageUrl())
                .webAppEntity(WebAppEntity.builder().applicationId(storage.getId().getValue().toString()).build())
                .build();
    }

    public Storage storageEntityToStorageEntity(StorageEntity storageEntity) {
        return Storage
                .builder()
                .storageName(storageEntity.getStorageName())
                .storageUrl(storageEntity.getStorageUrl())
                .applicationId(new ApplicationId(UUID.fromString(storageEntity.getApplicationId())))
                .build();
    }

    public WebAppEntity webAppEntityToWebAppEntity(WebApp webApp,DockerContainerEntity dockerContainerEntity,
                                                   StorageEntity storageEntity) {
        return WebAppEntity.builder()
                .applicationId(webApp.getId().getValue().toString())
                .applicationName(webApp.getApplicationName().getValue())
                .userId(webApp.getId().getValue().toString())
                .error(webApp.getErrorMessages())
                .applicationStatus(webApp.getApplicationStatus())
                .dockerContainerEntity(dockerContainerEntity)
                .storageEntity(storageEntity)
                .javaVersion(webApp.getJavaVersion().getVersion())
                .serverPort(webApp.getServerPort().getValue())
                .build();
    }

    public WebApp webAppEntityToWebAppEntity(WebAppEntity webAppEntity) {
        return WebApp.builder()
                .dockerContainer(this.dockerContainerEntityToDockerContainer(webAppEntity.getDockerContainerEntity()))
                .storage(this.storageEntityToStorageEntity(webAppEntity.getStorageEntity()))
                .javaVersion(new JavaVersion(webAppEntity.getJavaVersion()))
                .serverPort(new ServerPort(webAppEntity.getServerPort()))
                .errorMessages(webAppEntity.getError())
                .userId(new UserId(webAppEntity.getUserId()))
                .applicationName(new ApplicationName(webAppEntity.getApplicationName()))
                .applicationId(new ApplicationId(UUID.fromString(webAppEntity.getApplicationId())))
                .applicationStatus(webAppEntity.getApplicationStatus())
                .build();
    }
}
