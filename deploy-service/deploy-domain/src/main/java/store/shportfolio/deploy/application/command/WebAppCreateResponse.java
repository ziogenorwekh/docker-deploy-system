package store.shportfolio.deploy.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebAppCreateResponse {
    private UUID applicationId;
    private String applicationName;
    private int javaVersion;
    private int serverPort;
    private ApplicationStatus applicationStatus;
}
