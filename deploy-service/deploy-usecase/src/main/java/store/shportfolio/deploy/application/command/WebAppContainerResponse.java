package store.shportfolio.deploy.application.command;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

@Getter
@Builder
public class WebAppContainerResponse {

    private final String applicationId;
    private final DockerContainerStatus dockerContainerStatus;
    private final String applicationName;
    private final String cpuUsage;
    private final String memoryUsage;
    private final String logs;

    public WebAppContainerResponse(String applicationId,
                                   DockerContainerStatus dockerContainerStatus,
                                   String applicationName, String cpuUsage, String memoryUsage, String logs) {
        this.applicationId = applicationId;
        this.dockerContainerStatus = dockerContainerStatus;
        this.applicationName = applicationName;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.logs = logs;
    }
}
