package store.shportfolio.deploy.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppTrackResponse {
    private UUID applicationId;
    private String applicationName;
    private int javaVersion;
    private int serverPort;
    private ApplicationStatus applicationStatus;
    private String userId;
    private String errorMessages;
    private String endPointUrl;
    private DockerContainerStatus dockerContainerStatus;
    private LocalDateTime createdAt;
}
