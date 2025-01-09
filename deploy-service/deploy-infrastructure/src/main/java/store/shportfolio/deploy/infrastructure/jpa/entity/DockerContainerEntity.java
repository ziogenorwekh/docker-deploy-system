package store.shportfolio.deploy.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

@Getter
@Entity
@ToString
@Table(name = "DOCKERCONTAINER_ENTITY")
@NoArgsConstructor
public class DockerContainerEntity {

    @Id
    @Column(nullable = false, name = "APPLICATION_ID", unique = true)
    private String applicationId;


    @Column(name = "DOCKERCONTAINER_ID")
    private String dockerContainerId;

    @Column(name = "IMAGE_ID")
    private String imageId;

    @Column(name = "ENDPOINT_URL", nullable = false)
    private String endPointUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCKERCONTAINER_STATUS")
    private DockerContainerStatus dockerContainerStatus;

    @Builder
    public DockerContainerEntity(String applicationId, String dockerContainerId, String imageId,
                                 String endPointUrl, DockerContainerStatus dockerContainerStatus) {
        this.applicationId = applicationId;
        this.dockerContainerId = dockerContainerId;
        this.imageId = imageId;
        this.endPointUrl = endPointUrl;
        this.dockerContainerStatus = dockerContainerStatus;
    }
}
