package store.shportfolio.deploy.application.command;

import lombok.*;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

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
    private LocalDateTime createdAt;
}
