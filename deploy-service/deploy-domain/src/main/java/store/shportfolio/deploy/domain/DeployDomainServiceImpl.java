package store.shportfolio.deploy.domain;

import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.WebAppCreateCommand;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;


public class DeployDomainServiceImpl implements DeployDomainService {

    @Override
    public WebApp createWebApp(UserGlobal userGlobal, WebAppCreateCommand webAppCreateCommand) {
        return WebApp.createWebApp(userGlobal.getUserId(), webAppCreateCommand.getApplicationName(),
                webAppCreateCommand.getPort(), webAppCreateCommand.getVersion());
    }

    @Override
    public DockerContainer createDockerContainer(ApplicationId applicationId) {
        return DockerContainer.initializeDockerContainer(applicationId);
    }

    @Override
    public void successfulCreateDockerContainer(DockerContainer dockerContainer, DockerCreated dockerCreated) {
        dockerContainer.initializedDockerContainer(dockerCreated);
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
    public void failedCreateApplication(WebApp webApp,String error) {
        webApp.failedApplication(error);
    }

    @Override
    public void saveStorageInfo(Storage storage, String storageUrl,String storageName) {
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
}
