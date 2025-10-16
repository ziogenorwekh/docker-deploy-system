package store.shportfolio.deploy.infrastructure.docker.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.dto.DockerCreated;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.application.exception.DockerContainerException;
import store.shportfolio.deploy.application.exception.WebAppException;
import store.shportfolio.deploy.application.ports.output.docker.DockerConnector;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerContainerHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerImageCreateHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerResourceHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerfileCreateHelper;

import java.io.File;

@Slf4j
@Component
public class DockerConnectorImpl implements DockerConnector {

    private final DockerfileCreateHelper dockerfileCreateHelper;
    private final DockerImageCreateHelper dockerImageCreateHelper;
    private final DockerContainerHelper dockerContainerHelper;
    private final DockerResourceHelper dockerResourceHelper;
    @Value("${user.docker.server.endpoint}")
    private String endpointUrl;

    @Autowired
    public DockerConnectorImpl(DockerfileCreateHelper dockerfileCreateHelper,
                               DockerImageCreateHelper dockerImageCreateHelper,
                               DockerContainerHelper dockerContainerHelper,
                               DockerResourceHelper dockerResourceHelper) {
        this.dockerfileCreateHelper = dockerfileCreateHelper;
        this.dockerImageCreateHelper = dockerImageCreateHelper;
        this.dockerContainerHelper = dockerContainerHelper;
        this.dockerResourceHelper = dockerResourceHelper;
    }

    @Override
    public DockerCreated createContainer(WebApp webApp, String storageUrl) {
        File dockerfile = null;
        String imageId = null;
        String dockerId = null;
        dockerfile = dockerfileCreateHelper.createDockerfile(webApp, storageUrl);
        imageId = dockerImageCreateHelper.createImage(webApp, dockerfile);
        dockerId = dockerContainerHelper.runContainer(imageId, webApp);
        log.info("time wait 8 seconds");
        try {
            Thread.sleep(1000L * 8);
        } catch (InterruptedException e) {
            throw new WebAppException("Thread interrupted: " + e.getMessage());
        }
        try {
            if (dockerContainerHelper.isContainerRunning(dockerId)) {
                return DockerCreated
                        .builder()
                        .dockerContainerStatus(DockerContainerStatus.STARTED)
                        .error("")
                        .endPointUrl(String.format("%s:%s", endpointUrl, webApp.getServerPort().getValue()))
                        .dockerImageId(imageId)
                        .dockerContainerId(dockerId)
                        .build();
            }
        } catch (Exception e) {
            String tracked = dockerContainerHelper.trackLogContainer(dockerId);
            return DockerCreated
                    .builder()
                    .dockerContainerStatus(DockerContainerStatus.ERROR)
                    .error(tracked)
                    .endPointUrl(String.format("%s:%s", endpointUrl, webApp.getServerPort().getValue()))
                    .dockerImageId(imageId)
                    .dockerContainerId(dockerId)
                    .build();
        } finally {
            dockerfileCreateHelper.deleteLocalDockerfile(dockerfile);
        }
        return null;
    }
    @Override
    public ResourceUsage getResourceUsage(String dockerContainerId) {
        return dockerResourceHelper.getContainerResources(dockerContainerId);
    }

    @Override
    public String trackLogs(String dockerContainerId) {
        return dockerContainerHelper.trackLogContainer(dockerContainerId);
    }

    @Override
    public Boolean startContainer(String dockerContainerId) {
        return dockerContainerHelper.startContainer(dockerContainerId);
    }

    @Override
    public Boolean stopContainer(String dockerContainerId) {
        return dockerContainerHelper.stopContainer(dockerContainerId);
    }

    @Override
    public void dropContainer(String dockerContainerId) {
        dockerContainerHelper.dropContainer(dockerContainerId);
    }

    @Override
    public void removeImage(String imageId) {
        dockerContainerHelper.removeImage(imageId);
    }
}
