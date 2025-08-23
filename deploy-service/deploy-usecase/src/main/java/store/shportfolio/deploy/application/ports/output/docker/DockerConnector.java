package store.shportfolio.deploy.application.ports.output.docker;

import store.shportfolio.deploy.application.dto.DockerCreated;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.domain.entity.WebApp;

public interface DockerConnector {

    DockerCreated createContainer(WebApp webApp, String storageUrl);

    ResourceUsage getResourceUsage(String dockerContainerId);

    String trackLogs(String dockerContainerId);

    Boolean startContainer(String dockerContainerId);

    Boolean stopContainer(String dockerContainerId);

    void dropContainer(String dockerContainerId);

    void removeImage(String imageId);
}
