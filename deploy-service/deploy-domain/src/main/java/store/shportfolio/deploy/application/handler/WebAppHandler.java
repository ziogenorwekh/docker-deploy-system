package store.shportfolio.deploy.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.WebAppCreateCommand;
import store.shportfolio.deploy.application.exception.ApplicationNotFoundException;
import store.shportfolio.deploy.application.ports.output.repository.WebAppRepository;
import store.shportfolio.deploy.domain.DeployDomainService;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;

import java.util.UUID;

@Component
public class WebAppHandler {

    private final WebAppRepository webAppRepository;
    private final DeployDomainService deployDomainService;

    @Autowired
    public WebAppHandler(WebAppRepository webAppRepository,
                         DeployDomainService deployDomainService) {
        this.webAppRepository = webAppRepository;
        this.deployDomainService = deployDomainService;
    }


    public WebApp createWebApp(UserGlobal userGlobal, WebAppCreateCommand webAppCreateCommand) {
        return deployDomainService.createWebApp(userGlobal, webAppCreateCommand);
    }

    public void startContainerizing(WebApp webApp, Storage storage) {
        deployDomainService.createdToContainerizing(webApp);
        this.saveWebApp(webApp, storage);
    }

    public void completeContainerizing(WebApp webApp, DockerContainer dockerContainer) {
        deployDomainService.containerizingToComplete(webApp);
        this.saveWebApp(webApp, dockerContainer);
    }

    public void failedApplication(WebApp webApp, String errorMessage) {
        deployDomainService.failedCreateApplication(webApp, errorMessage);
        this.saveWebApp(webApp,webApp.getDockerContainer(),webApp.getStorage());
    }


    public WebApp saveWebApp(WebApp webApp, DockerContainer dockerContainer, Storage storage) {
        deployDomainService.updateDockerContainer(webApp, dockerContainer);
        deployDomainService.updateStorage(webApp, storage);
        return webAppRepository.save(webApp);
    }

    public void saveWebApp(WebApp webApp, DockerContainer dockerContainer) {
        deployDomainService.updateDockerContainer(webApp, dockerContainer);
        webAppRepository.save(webApp);
    }

    public void saveWebApp(WebApp webApp, Storage storage) {
        deployDomainService.updateStorage(webApp, storage);
        webAppRepository.save(webApp);
    }

    public WebApp getWebApp(UUID applicationId) {
        return webAppRepository.findByApplicationId(applicationId).orElseThrow(() ->
                new ApplicationNotFoundException(String.format("WebApp with applicationId %s not found", applicationId)));
    }

    public void deleteWebApp(WebApp webApp) {
        webAppRepository.deleteByApplicationId(webApp.getId().getValue());
    }
}
