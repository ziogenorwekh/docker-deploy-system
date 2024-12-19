package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.common.domain.entitiy.BaseEntity;
import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerId;

import java.util.UUID;

@Getter
public class DockerContainer extends BaseEntity<ApplicationId> {

    private DockerContainerId dockerContainerId;
    private String endPointUrl;
    private DockerContainerStatus dockerContainerStatus;

    @Builder
    public DockerContainer(
                           ApplicationId applicationId,
                           DockerContainerId dockerContainerId,
                           String endPointUrl,
                           DockerContainerStatus dockerContainerStatus) {
        super.setId(applicationId);
        this.dockerContainerId = dockerContainerId;
        this.endPointUrl = endPointUrl;
        this.dockerContainerStatus = dockerContainerStatus;
    }

    public static DockerContainer initializeDockerContainer(ApplicationId applicationId) {
        return DockerContainer.builder()
                .applicationId(applicationId)
                .dockerContainerId(new DockerContainerId(""))
                .dockerContainerStatus(DockerContainerStatus.INITIALIZED)
                .endPointUrl("")
                .build();
    }

    public void initializedDockerContainer(DockerCreated dockerCreated) {
        this.dockerContainerId = new DockerContainerId(dockerCreated.getDockerContainerId());
        this.dockerContainerStatus = dockerCreated.getDockerContainerStatus();
    }

    public void startDockerContainer() {
        if (this.dockerContainerStatus == DockerContainerStatus.STOPPED ||
                this.dockerContainerStatus == DockerContainerStatus.INITIALIZED) {
            this.dockerContainerStatus = DockerContainerStatus.STARTED;
        }
    }

    public void stopDockerContainer() {
        if (this.dockerContainerStatus == DockerContainerStatus.STARTED) {
            this.dockerContainerStatus = DockerContainerStatus.STOPPED;
        }
    }
}
