package store.shportfolio.deploy.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.ApplicationNotFoundException;
import store.shportfolio.deploy.application.exception.ContainerAccessException;
import store.shportfolio.deploy.application.exception.DockerNotFoundException;
import store.shportfolio.deploy.application.ports.output.docker.DockerConnector;
import store.shportfolio.deploy.application.ports.output.repository.DockerContainerRepository;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.DeployDomainService;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.util.UUID;

@Slf4j
@Component
public class DockerContainerHandler {

    private final DockerConnector dockerConnector;
    private final DockerContainerRepository dockerContainerRepository;
    private final DeployDomainService deployDomainService;

    @Autowired
    public DockerContainerHandler(DockerConnector dockerConnector,
                                  DockerContainerRepository dockerContainerRepository,
                                  DeployDomainService deployDomainService) {
        this.dockerConnector = dockerConnector;
        this.dockerContainerRepository = dockerContainerRepository;
        this.deployDomainService = deployDomainService;
    }

    public DockerContainer createDockerContainer(WebApp webApp) {
        return deployDomainService.createDockerContainer(webApp.getId());
    }

    public void saveDockerContainer(DockerContainer dockerContainer) {
        dockerContainerRepository.save(dockerContainer);
    }

    public DockerContainer getDockerContainer(UUID applicationId) {
        return dockerContainerRepository.findByApplicationId(applicationId).orElseThrow(()->
                new DockerNotFoundException("docker not found by id: " + applicationId));
    }

    // need additional logic
    public DockerContainer createDockerImageAndRun(WebApp webApp,String storageUrl) {
        DockerContainer dockerContainer = this.getDockerContainer(webApp.getId().getValue());

        if (!(dockerContainer.getDockerContainerStatus() == DockerContainerStatus.INITIALIZED)) {
            throw new ContainerAccessException("Docker container is not initialized");
        }

        log.info("docker container must be INITIALIZED -> {}", dockerContainer.getDockerContainerStatus());

        DockerCreated dockerCreated = dockerConnector.createContainer(webApp, storageUrl);
        deployDomainService.successfulCreateDockerContainer(dockerContainer, dockerCreated);
        DockerContainer saved = dockerContainerRepository.save(dockerContainer);

        log.info("docker container must be STARTED -> {}", saved.getDockerContainerStatus());
        return saved;
    }


    public String getContainerLogs(DockerContainer dockerContainer) {
        return dockerConnector.trackLogs(dockerContainer.getDockerContainerId().getValue());
    }

    public DockerContainer startContainer(DockerContainer dockerContainer) {

        if (dockerContainer.getDockerContainerStatus() == DockerContainerStatus.STARTED) {
            throw new ContainerAccessException("Container already started");
        }

        if (dockerConnector.startContainer(dockerContainer.getDockerContainerId().getValue())) {
            deployDomainService.startDockerContainer(dockerContainer);
            return dockerContainerRepository.save(dockerContainer);
        } else {
            throw new ContainerAccessException(String.format("Container %s is not stop",
                    dockerContainer.getDockerContainerId().getValue()));
        }
    }

    public DockerContainer stopContainer(DockerContainer dockerContainer) {
        if (dockerContainer.getDockerContainerStatus() == DockerContainerStatus.STOPPED) {
            throw new ContainerAccessException("Container already stopped");
        }

        if (dockerConnector.stopContainer(dockerContainer.getDockerContainerId().getValue())) {
            deployDomainService.stopDockerContainer(dockerContainer);
            return dockerContainerRepository.save(dockerContainer);
        } else {
            throw new ContainerAccessException(String.format("Container %s is not running",
                    dockerContainer.getDockerContainerId().getValue()));
        }
    }


    public ResourceUsage getContainerUsage(DockerContainer dockerContainer) {
        return dockerConnector.getResourceUsage(dockerContainer.getDockerContainerId().getValue());
    }

    public void deleteDockerContainer(UUID applicationId) {
        dockerContainerRepository.removeByApplicationId(applicationId);
    }
}
