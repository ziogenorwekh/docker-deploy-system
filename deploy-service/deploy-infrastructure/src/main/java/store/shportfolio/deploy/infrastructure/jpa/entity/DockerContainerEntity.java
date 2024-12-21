package store.shportfolio.deploy.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

@Getter
@Entity
@NoArgsConstructor
public class DockerContainerEntity {


    @Id
    private String applicationId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "APPLICATION_ID")
    private WebAppEntity webAppEntity;


    private String dockerContainerId;

    @Column(name = "ENDPOINT_URL", nullable = false)
    private String endPointUrl;

    @Enumerated(EnumType.STRING)
    private DockerContainerStatus dockerContainerStatus;

    @Builder
    public DockerContainerEntity(String applicationId, WebAppEntity webAppEntity, String dockerContainerId,
                                 String endPointUrl, DockerContainerStatus dockerContainerStatus) {
        this.applicationId = applicationId;
        this.webAppEntity = webAppEntity;
        this.dockerContainerId = dockerContainerId;
        this.endPointUrl = endPointUrl;
        this.dockerContainerStatus = dockerContainerStatus;
    }
}
