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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false,name = "APPLICATION_ID")
    private String applicationId;


    @Column(name = "DOCKERCONTAINER_ID")
    private String dockerContainerId;

    @Column(name = "ENDPOINT_URL", nullable = false)
    private String endPointUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCKERCONTAINER_STATUS")
    private DockerContainerStatus dockerContainerStatus;

    @Builder
    public DockerContainerEntity(String applicationId, String dockerContainerId,
                                 String endPointUrl, DockerContainerStatus dockerContainerStatus) {
        this.applicationId = applicationId;
        this.dockerContainerId = dockerContainerId;
        this.endPointUrl = endPointUrl;
        this.dockerContainerStatus = dockerContainerStatus;
    }
}
