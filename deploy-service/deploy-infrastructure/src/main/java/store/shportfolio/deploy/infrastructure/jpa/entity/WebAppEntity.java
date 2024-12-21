package store.shportfolio.deploy.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

@Getter
@Entity
@NoArgsConstructor
public class WebAppEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String applicationId;

    @Column(nullable = false)
    private String userId;

    @OneToOne(mappedBy = "dockerContainerEntity", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private DockerContainerEntity dockerContainerEntity;

    @OneToOne(mappedBy = "storageEntity",
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StorageEntity storageEntity;

    @Column(nullable = false)
    private String applicationName;

    @Column(nullable = false, name = "SERVER_PORT")
    private int serverPort;

    @Column(nullable = false, name = "JAVA_VERSION")
    private int javaVersion;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;

    @Lob
    @Column(name = "ERROR_MESSAGE")
    private String error;

    @Builder
    public WebAppEntity(String applicationId, String userId, DockerContainerEntity dockerContainerEntity,
                        StorageEntity storageEntity, String applicationName,
                        int serverPort, int javaVersion, ApplicationStatus applicationStatus, String error) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.dockerContainerEntity = dockerContainerEntity;
        this.storageEntity = storageEntity;
        this.applicationName = applicationName;
        this.serverPort = serverPort;
        this.javaVersion = javaVersion;
        this.applicationStatus = applicationStatus;
        this.error = error;
    }
}
