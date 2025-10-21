package store.shportfolio.deploy.domain;

import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.StorageName;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

public class DeployDomainServiceImpl implements DeployDomainService {

    @Override
    public WebApp createWebApp(UserGlobal userGlobal,String applicationName, int port, int version ) {
        return WebApp.createWebApp(userGlobal.getUserId(), applicationName, port, version);
    }

    @Override
    public DockerContainer createDockerContainer(ApplicationId applicationId) {
        return DockerContainer.initializeDockerContainer(applicationId);
    }

    @Override
    public void successfulCreateDockerContainer(DockerContainer dockerContainer, String dockerContainerId,
                                                DockerContainerStatus dockerContainerStatus,
                                                String dockerImageId,
                                                String endPointUrl) {
        dockerContainer.successfulDockerContainer(dockerContainerId, dockerContainerStatus, dockerImageId, endPointUrl);
    }

    @Override
    public Storage createStorage(ApplicationId applicationId) {
        return Storage.createStorage(applicationId);
    }

    @Override
    public void createdToContainerizing(WebApp webApp) {
        webApp.updateCreatedToContainerizing();
    }

    @Override
    public void containerizingToComplete(WebApp webApp) {
        webApp.updateContainerizingToCompleted();
    }

    @Override
    public void failedCreateApplication(WebApp webApp, String error) {
        webApp.failedApplication(error);
    }

    @Override
    public void saveStorageInfo(Storage storage, StorageName storageName, StorageUrl storageUrl) {
        storage.savedStorage(storageUrl, storageName);
    }

    @Override
    public void startDockerContainer(DockerContainer dockerContainer) {
        dockerContainer.startDockerContainer();
    }

    @Override
    public void stopDockerContainer(DockerContainer dockerContainer) {
        dockerContainer.stopDockerContainer();
    }

    @Override
    public void reDeployApplication(WebApp webApp) {
        webApp.reDeployApplicationStatus();
    }

    @Override
    public void reInitializeDockerContainer(DockerContainer dockerContainer) {
        dockerContainer.reInitializeDockerContainer();
    }

    @Override
    public void stoppedUnKnownReasonDockerContainer(DockerContainer dockerContainer) {
        dockerContainer.stoppedDockerContainer();
    }
}
