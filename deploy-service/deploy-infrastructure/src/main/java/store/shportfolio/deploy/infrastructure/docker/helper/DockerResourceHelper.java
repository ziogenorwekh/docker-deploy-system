package store.shportfolio.deploy.infrastructure.docker.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.vo.ResourceUsage;

import java.io.IOException;

@Component
public class DockerResourceHelper {

    private final DockerClient dockerClient;

    public DockerResourceHelper(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public ResourceUsage getContainerResources(String containerId)  {
        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();

        dockerClient.statsCmd(containerId).exec(callback);
        try {
            Statistics stats = callback.awaitResult();
            if (stats != null) {
                // CPU Usage Calculation
                long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage()
                        - stats.getPreCpuStats().getCpuUsage().getTotalUsage();
                long systemCpuDelta = stats.getCpuStats().getSystemCpuUsage()
                        - stats.getPreCpuStats().getSystemCpuUsage();
                int cpuCount = stats.getCpuStats().getOnlineCpus() != null
                        ? stats.getCpuStats().getOnlineCpus().intValue()
                        : 1;
                double cpuUsage = (double) cpuDelta / systemCpuDelta * cpuCount * 100;

                // Memory Usage Calculation
                long memoryUsage = stats.getMemoryStats().getUsage();
                long memoryLimit = stats.getMemoryStats().getLimit();
                double memoryUsagePercentage = (double) memoryUsage / memoryLimit * 100;

                return ResourceUsage.builder()
                        .cpuUsage(String.valueOf(cpuUsage))
                        .memoryUsage(String.valueOf(memoryUsagePercentage))
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get container statistics for ID: " + containerId, e);
        }

        return ResourceUsage.builder()
                .cpuUsage(String.valueOf(0.0))
                .memoryUsage(String.valueOf(0.0))
                .build();
    }
}
