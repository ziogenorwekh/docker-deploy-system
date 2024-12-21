package store.shportfolio.deploy.application.ports.output.docker;

import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.WebApp;

public interface DockerConnector {

    DockerCreated createContainer(WebApp webApp, String storageUrl);

    ResourceUsage getResourceUsage(String dockerContainerId);

    String trackLogs(String dockerContainerId);

    Boolean startContainer(String dockerContainerId);

    Boolean stopContainer(String dockerContainerId);

    void dropContainer(String dockerContainerId);

}
