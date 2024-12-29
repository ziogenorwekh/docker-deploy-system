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
    private final String dockerImageId;
    private final String endPointUrl;

    public DockerCreated(String dockerContainerId,
                         DockerContainerStatus dockerContainerStatus, String error, String dockerImageId, String endPointUrl) {
        this.dockerContainerId = dockerContainerId;
        this.dockerImageId = dockerImageId;
        this.dockerContainerStatus = dockerContainerStatus;
        this.error = error;
        this.endPointUrl = endPointUrl;
    }
}
