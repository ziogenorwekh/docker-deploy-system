package store.shportfolio.deploy.infrastructure.docker.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.ports.output.docker.DockerConnector;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerContainerHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerImageCreateHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerResourceHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerfileCreateHelper;

import java.io.File;

@Component
public class DockerConnectorImpl implements DockerConnector {

    private final DockerfileCreateHelper dockerfileCreateHelper;
    private final DockerImageCreateHelper dockerImageCreateHelper;
    private final DockerContainerHelper dockerContainerHelper;
    private final DockerResourceHelper dockerResourceHelper;
    @Value("docker.server.endpoint")
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
        try {
            File dockerfile = dockerfileCreateHelper.createDockerfile(webApp, storageUrl);
            String imageId = dockerImageCreateHelper.createImage(webApp, dockerfile);
            String dockerId = dockerContainerHelper.runContainer(imageId, webApp);
            dockerfileCreateHelper.deleteLocalDockerfile(dockerfile);
            return DockerCreated
                    .builder()
                    .dockerContainerStatus(DockerContainerStatus.STARTED)
                    .error("")
                    .endPointUrl(String.format("%s:%s", endpointUrl, webApp.getServerPort().getValue()))
                    .dockerImageId(imageId)
                    .dockerContainerId(dockerId)
                    .build();
        } catch (Exception e) {
            return DockerCreated
                    .builder()
                    .dockerContainerStatus(DockerContainerStatus.ERROR)
                    .dockerContainerId("")
                    .endPointUrl("")
                    .dockerImageId("")
                    .error(e.getMessage())
                    .build();
        }
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
    public void dropContainer(String dockerContainerId, String imageId) {
        dockerContainerHelper.dropContainerAndRemoveImage(dockerContainerId, imageId);
    }
}
