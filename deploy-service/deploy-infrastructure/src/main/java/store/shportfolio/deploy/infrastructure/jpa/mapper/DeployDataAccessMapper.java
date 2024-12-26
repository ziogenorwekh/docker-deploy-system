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

    // DockerContainerEntity를 DockerContainer로 변환
    public DockerContainerEntity dockerContainerToDockerContainerEntity(DockerContainer dockerContainer) {
        return DockerContainerEntity
                .builder()
                .dockerContainerId(dockerContainer.getDockerContainerId().getValue() != null ?
                        dockerContainer.getDockerContainerId().getValue() : null)
                .dockerContainerStatus(dockerContainer.getDockerContainerStatus() != null ? dockerContainer.getDockerContainerStatus() : null)
                .applicationId(dockerContainer.getId().getValue().toString() != null ? dockerContainer.getId().getValue().toString() : null)
                .endPointUrl(dockerContainer.getEndPointUrl() != null ? dockerContainer.getEndPointUrl() : null)
                .build();
    }

    // DockerContainerEntity를 DockerContainer로 변환
    public DockerContainer dockerContainerEntityToDockerContainer(DockerContainerEntity dockerContainerEntity) {

        return DockerContainer.builder()
                .dockerContainerStatus(dockerContainerEntity.getDockerContainerStatus() != null ?
                        dockerContainerEntity.getDockerContainerStatus() : null) // null일 수 있음
                .applicationId(new ApplicationId(UUID.fromString(dockerContainerEntity.getApplicationId())))
                .endPointUrl(dockerContainerEntity.getEndPointUrl() != null ?
                        dockerContainerEntity.getEndPointUrl() : null)
                .dockerContainerId(dockerContainerEntity.getDockerContainerId() != null ?
                        new DockerContainerId(dockerContainerEntity.getDockerContainerId()) : null)
                .build();
    }

    // StorageEntity를 Storage로 변환
    public StorageEntity storageEntityToStorage(Storage storage) {
        return StorageEntity.builder()
                .applicationId(storage.getId().getValue().toString())
                .storageName(storage.getStorageName() != null ? storage.getStorageName() : null)
                .storageUrl(storage.getStorageUrl() != null ? storage.getStorageUrl() : null)
                .build();
    }

    // StorageEntity를 Storage로 변환
    public Storage storageEntityToStorageEntity(StorageEntity storageEntity) {
        return Storage
                .builder()
                .storageName(storageEntity.getStorageName() != null ? storageEntity.getStorageName() : null)
                .storageUrl(storageEntity.getStorageUrl() != null ? storageEntity.getStorageUrl() : null)
                .applicationId(new ApplicationId(UUID.fromString(storageEntity.getApplicationId())))
                .build();
    }

    // WebApp을 WebAppEntity로 변환
    public WebAppEntity webAppEntityToWebAppEntity(WebApp webApp) {
        return WebAppEntity.builder()
                .applicationId(webApp.getId().getValue().toString())
                .applicationName(webApp.getApplicationName().getValue())
                .userId(webApp.getId().getValue().toString())
                .error(webApp.getErrorMessages())
                .applicationStatus(webApp.getApplicationStatus())
                .javaVersion(webApp.getJavaVersion().getVersion())
                .serverPort(webApp.getServerPort().getValue())
                .createdAt(webApp.getCreatedAt())
                .build();
    }

    // WebAppEntity를 WebApp으로 변환
    public WebApp webAppEntityToWebAppEntity(WebAppEntity webAppEntity) {

        return WebApp.builder()
                .javaVersion(new JavaVersion(webAppEntity.getJavaVersion()))
                .serverPort(new ServerPort(webAppEntity.getServerPort()))
                .errorMessages(webAppEntity.getError())
                .userId(new UserId(webAppEntity.getUserId()))
                .applicationName(new ApplicationName(webAppEntity.getApplicationName()))
                .applicationId(new ApplicationId(UUID.fromString(webAppEntity.getApplicationId())))
                .applicationStatus(webAppEntity.getApplicationStatus())
                .createdAt(webAppEntity.getCreatedAt())
                .build();
    }
}