package store.shportfolio.deploy.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class WebAppContainerResponse {

    private final String applicationId;
    private final String applicationName;
    private final String cpuUsage;
    private final String memoryUsage;
    private final String logs;

    public WebAppContainerResponse(String applicationId, String applicationName, String cpuUsage, String memoryUsage, String logs) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.logs = logs;
    }
}
