package store.shportfolio.deploy.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import store.shportfolio.deploy.application.dto.DockerCreated;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.application.exception.*;
import store.shportfolio.deploy.application.ports.output.docker.DockerConnector;
import store.shportfolio.deploy.application.ports.output.repository.DockerContainerRepository;
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

    @Transactional(readOnly = true)
    public DockerContainer getDockerContainer(UUID applicationId) {
        return dockerContainerRepository.findByApplicationId(applicationId).orElseThrow(() ->
                new DockerNotFoundException("docker not found by id: " + applicationId));
    }

    @Transactional
    public DockerContainer createDockerImageAndRun(WebApp webApp, String storageUrl) {
        return handleDockerContainerCreation(webApp, storageUrl,false);
    }

    @Transactional
    public DockerContainer reCreateDockerImageAndRun(WebApp webApp, String storageUrl) {
        return handleDockerContainerCreation(webApp, storageUrl, true);
    }


    public String getContainerLogs(DockerContainer dockerContainer) {
        return dockerConnector.trackLogs(dockerContainer.getDockerContainerId().getValue());
    }

    public DockerContainer startContainer(DockerContainer dockerContainer) {
        if (dockerContainer.getDockerContainerStatus() == DockerContainerStatus.ERROR) {
            throw new DockerContainerErrorException("Container status error");
        }

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
        if (dockerContainer.getDockerContainerStatus() == DockerContainerStatus.ERROR) {
            throw new DockerContainerErrorException("Container status error");
        }

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
        DockerContainer dockerContainer = this.getDockerContainer(applicationId);
        try {
            dockerConnector.dropContainer(dockerContainer.getDockerContainerId().getValue());
            dockerConnector.removeImage(dockerContainer.getImageId());
        } catch (Exception e) {
            log.error("Error dropping docker container, message is {}", e.getMessage());
        }
        dockerContainerRepository.removeByApplicationId(applicationId);
    }

    private DockerContainer handleDockerContainerCreation(WebApp webApp,String storageUrl, boolean isReCreate) {
        DockerContainer dockerContainer;
        if (isReCreate) {
            this.deleteDockerContainer(webApp.getId().getValue());

            dockerContainerRepository.flush();
            dockerContainerRepository.clear();
            dockerContainer = deployDomainService.createDockerContainer(webApp.getId());
            log.info("Re-create docker container initialized -> {}", dockerContainer.getDockerContainerStatus());
        } else {
            dockerContainer = this.getDockerContainer(webApp.getId().getValue());
            if (dockerContainer.getDockerContainerStatus() != DockerContainerStatus.INITIALIZED) {
                throw new ContainerAccessException("Docker container is not initialized");
            }
            log.info("Create docker container, must be INITIALIZED -> {}", dockerContainer.getDockerContainerStatus());
        }

        DockerCreated dockerCreated = dockerConnector.createContainer(webApp, storageUrl);
        log.info("Docker container created -> {}", dockerCreated.getDockerContainerStatus());
        deployDomainService.successfulCreateDockerContainer(
                dockerContainer,
                dockerCreated.getDockerContainerId(),
                dockerCreated.getDockerContainerStatus(),
                dockerCreated.getDockerImageId(),
                dockerCreated.getEndPointUrl()
        );
        DockerContainer saved = dockerContainerRepository.save(dockerContainer);
        log.info("Docker container must be STARTED -> {}", saved.getDockerContainerStatus());
        return saved;
    }
}
