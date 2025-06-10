package store.shportfolio.deploy.application.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ResourceUsage {

    private final String cpuUsage;
    private final String memoryUsage;

    public ResourceUsage(String cpuUsage, String memoryUsage) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
    }
}
