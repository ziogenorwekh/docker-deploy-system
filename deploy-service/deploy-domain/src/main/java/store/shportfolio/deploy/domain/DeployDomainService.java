package store.shportfolio.deploy.domain;

import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.StorageName;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

public interface DeployDomainService {

    WebApp createWebApp(UserGlobal userGlobal, String applicationName, int port, int version);

    DockerContainer createDockerContainer(ApplicationId applicationId);

    void successfulCreateDockerContainer(DockerContainer dockerContainer,
                                         String dockerContainerId,
                                         DockerContainerStatus dockerContainerStatus,
                                         String dockerImageId,
                                         String endPointUrl);

    Storage createStorage(ApplicationId applicationId);

    void createdToContainerizing(WebApp webApp);

    void containerizingToComplete(WebApp webApp);

    void failedCreateApplication(WebApp webApp,String error);

    void saveStorageInfo(Storage storage, StorageName storageName, StorageUrl storageUrl);

    void startDockerContainer(DockerContainer dockerContainer);

    void stopDockerContainer(DockerContainer dockerContainer);

    void reDeployApplication(WebApp webApp);

    void reInitializeDockerContainer(DockerContainer dockerContainer);

    void stoppedUnKnownReasonDockerContainer(DockerContainer dockerContainer);
}
