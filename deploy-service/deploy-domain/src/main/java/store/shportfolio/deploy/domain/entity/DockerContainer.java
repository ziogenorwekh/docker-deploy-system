package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import store.shportfolio.common.domain.entitiy.BaseEntity;
import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.deploy.application.dto.DockerCreated;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerId;

@Getter
@ToString
public class DockerContainer extends BaseEntity<ApplicationId> {

    private DockerContainerId dockerContainerId;
    private String endPointUrl;
    private DockerContainerStatus dockerContainerStatus;
    private String imageId;

    @Builder
    public DockerContainer(
            ApplicationId applicationId,
            DockerContainerId dockerContainerId,
            String endPointUrl,
            DockerContainerStatus dockerContainerStatus, String imageId) {
        this.imageId = imageId;
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
                .imageId("")
                .build();
    }

    public void successfulDockerContainer(DockerCreated dockerCreated) {
        this.dockerContainerId = new DockerContainerId(dockerCreated.getDockerContainerId());
        this.dockerContainerStatus = dockerCreated.getDockerContainerStatus();
        this.imageId = dockerCreated.getDockerImageId();
        this.endPointUrl = dockerCreated.getEndPointUrl();
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
