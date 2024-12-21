package store.shportfolio.deploy.application.vo;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.util.UUID;

@Getter
@Builder
public class DockerCreated {

    private final String dockerContainerId;
    private final DockerContainerStatus dockerContainerStatus;
    private final String error;

    public DockerCreated(String dockerContainerId,
                         DockerContainerStatus dockerContainerStatus, String error) {
        this.dockerContainerId = dockerContainerId;
        this.dockerContainerStatus = dockerContainerStatus;
        this.error = error;
    }
}
