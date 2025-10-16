package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import store.shportfolio.common.domain.entitiy.BaseEntity;
import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerId;

@Getter
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

    public void reInitializeDockerContainer() {
        this.dockerContainerId = new DockerContainerId("");
        this.dockerContainerStatus = DockerContainerStatus.INITIALIZED;
        this.endPointUrl = "";
        this.imageId = "";
    }

    public void successfulDockerContainer(String dockerContainerId,
                                          DockerContainerStatus dockerContainerStatus,
                                          String dockerImageId,
                                          String endPointUrl) {
        this.dockerContainerId = new DockerContainerId(dockerContainerId);
        this.dockerContainerStatus = dockerContainerStatus;
        this.imageId = dockerImageId;
        this.endPointUrl = endPointUrl;
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

    @Override
    public String toString() {
        return "DockerContainer{" +
                "dockerContainerId=" + dockerContainerId.getValue() +
                ", endPointUrl='" + endPointUrl.toString() + '\'' +
                ", dockerContainerStatus=" + dockerContainerStatus +
                ", imageId='" + imageId + '\'' +
                '}';
    }
}
