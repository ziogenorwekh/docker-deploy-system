package store.shportfolio.deploy.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@Table(name = "WEBAPP_ENTITY")
@NoArgsConstructor
public class WebAppEntity {

    @Id
    @Column(name = "APPLICATION_ID", unique = true, nullable = false)
    private String applicationId;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(name = "APPLICATION_NAME", nullable = false, unique = true)
    private String applicationName;

    @Column(nullable = false, name = "SERVER_PORT")
    private int serverPort;

    @Column(nullable = false, name = "JAVA_VERSION")
    private int javaVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "APPLICATION_STATUS")
    private ApplicationStatus applicationStatus;

    @Lob
    @Column(name = "ERROR_MESSAGE")
    private String error;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    public WebAppEntity(String applicationId, String userId, String applicationName,
                        int serverPort, int javaVersion, ApplicationStatus applicationStatus,
                        String error, LocalDateTime createdAt) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.applicationName = applicationName;
        this.serverPort = serverPort;
        this.javaVersion = javaVersion;
        this.applicationStatus = applicationStatus;
        this.error = error;
        this.createdAt = createdAt;
    }
}
