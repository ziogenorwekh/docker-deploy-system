package store.shportfolio.deploy.infrastructure.docker.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Statistics;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.DockerContainerException;
import store.shportfolio.deploy.application.dto.ResourceUsage;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class DockerResourceHelper {

    private final DockerClient dockerClient;
    private final String per = "%";

    public DockerResourceHelper(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public ResourceUsage getContainerResources(String containerId) {
        if (!isContainerRunning(containerId)) {
            return ResourceUsage.builder()
                    .memoryUsage("Not running")
                    .cpuUsage("Not running")
                    .build();
        }

        StatsCmd command = dockerClient.statsCmd(containerId);
        CompletableFuture<Statistics> aSynchronized = synchronizeDockerConnection(command);
        Statistics statistics = null;
        try {
            statistics = aSynchronized.get();
            long currentCpuUsage = statistics.getCpuStats().getCpuUsage().getTotalUsage().longValue();
            Long systemCpuUsage = statistics.getCpuStats().getSystemCpuUsage();
            String cpuPercentage = calcCpuUsage(currentCpuUsage, systemCpuUsage);

            long memoryUsage = statistics.getMemoryStats().getUsage();
            String memory = calcMemoryUsage(memoryUsage);
            return ResourceUsage.builder()
                    .cpuUsage(cpuPercentage)
                    .memoryUsage(memory)
                    .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new DockerContainerException("Error getting container resources", e);
        }
    }

    private CompletableFuture<Statistics> synchronizeDockerConnection(StatsCmd command) {
        CompletableFuture<Statistics> futureStats = new CompletableFuture<>();
        command.exec(new ResultCallback<Statistics>() {
            @Override
            public void onStart(Closeable closeable) {
            }
            @SneakyThrows(IOException.class)
            @Override
            public void onNext(Statistics statistics) {
                futureStats.complete(statistics); // Get the first statistics and complete the future.
                close();
            }
            @Override
            public void onError(Throwable throwable) {
                futureStats.completeExceptionally(throwable);
            }
            @Override
            public void onComplete() {
            }
            @Override
            public void close() throws IOException {
            }
        });
        return futureStats;
    }

    private String calcCpuUsage(Long current, Long total) {
        double usage = (current / (double) total) * 100.0;
        return String.format("%.2f%s", usage, per);
    }

    private String calcMemoryUsage(Long rss) {
        long usage = rss / 1024 / 1024;
        double percentage = (usage / (double) 512) * 100.0;
        return String.format("%.2f%s", (double) percentage, per);
    }

    private boolean isContainerRunning(String containerId) {
        try {
            InspectContainerResponse exec = dockerClient.inspectContainerCmd(containerId).exec();
            return Boolean.TRUE.equals(exec.getState().getRunning());
        } catch (Exception e) {
            throw new DockerContainerException("error executing container", e);
        }
    }
}
