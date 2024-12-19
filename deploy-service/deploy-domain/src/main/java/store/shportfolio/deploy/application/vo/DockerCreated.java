package store.shportfolio.deploy.application.vo;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.util.UUID;

@Getter
@Builder
public class DockerCreated {

    private final UUID applicationId;
    private final String dockerContainerId;
    private final DockerContainerStatus dockerContainerStatus;

    public DockerCreated(UUID applicationId, String dockerContainerId, DockerContainerStatus dockerContainerStatus) {
        this.applicationId = applicationId;
        this.dockerContainerId = dockerContainerId;
        this.dockerContainerStatus = dockerContainerStatus;
    }
}
