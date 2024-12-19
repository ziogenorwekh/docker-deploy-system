package store.shportfolio.deploy.domain;

import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.WebAppCreateCommand;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;

public interface DeployDomainService {

    WebApp createWebApp(UserGlobal userGlobal, WebAppCreateCommand webAppCreateCommand);

    DockerContainer createDockerContainer(ApplicationId applicationId);


    void successfulCreateDockerContainer(DockerContainer dockerContainer, DockerCreated dockerCreated);

    Storage createStorage(ApplicationId applicationId);

    void createdToContainerizing(WebApp webApp);

    void containerizingToComplete(WebApp webApp);

    void failedCreateApplication(WebApp webApp,String error);

    void updateDockerContainer(WebApp webApp, DockerContainer dockerContainer);

    void updateStorage(WebApp webApp, Storage storage);

    void saveStorageInfo(Storage storage, String storageName, String storageUrl);

    void startDockerContainer(WebApp webApp, DockerContainer dockerContainer);

    void stopDockerContainer(WebApp webApp, DockerContainer dockerContainer);
}
