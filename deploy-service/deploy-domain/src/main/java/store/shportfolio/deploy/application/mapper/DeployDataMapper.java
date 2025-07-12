package store.shportfolio.deploy.application.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.command.WebAppContainerResponse;
import store.shportfolio.deploy.application.command.WebAppCreateResponse;
import store.shportfolio.deploy.application.command.WebAppTrackResponse;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

@Component
public class DeployDataMapper {


    public WebAppCreateResponse webAppToWebAppCreateResponse(WebApp webApp) {
        return WebAppCreateResponse.builder()
                .applicationId(webApp.getId().getValue())
                .applicationName(webApp.getApplicationName().getValue())
                .javaVersion(webApp.getJavaVersion().getVersion())
                .serverPort(webApp.getServerPort().getValue())
                .applicationStatus(webApp.getApplicationStatus())
                .createdAt(webApp.getCreatedAt())
                .build();
    }

    public WebAppTrackResponse webAppToWebAppTrackResponse(WebApp webApp, String endpointUrl
    , DockerContainerStatus dockerContainerStatus) {
        return WebAppTrackResponse.builder()
                .applicationId(webApp.getId().getValue())
                .applicationName(webApp.getApplicationName().getValue())
                .javaVersion(webApp.getJavaVersion().getVersion())
                .serverPort(webApp.getServerPort().getValue())
                .applicationStatus(webApp.getApplicationStatus())
                .endPointUrl(endpointUrl)
                .dockerContainerStatus(dockerContainerStatus)
                .errorMessages(webApp.getErrorMessages())
                .userId(webApp.getUserId().getValue())
                .createdAt(webApp.getCreatedAt())
                .build();
    }

    public WebAppContainerResponse webAppToWebAppContainerResponse(WebApp webApp,
                                                                   ResourceUsage resourceUsage,
                                                                   String logs,
                                                                   DockerContainerStatus dockerContainerStatus) {
        return WebAppContainerResponse.builder()
                .applicationId(webApp.getId().getValue().toString())
                .dockerContainerStatus(dockerContainerStatus)
                .applicationName(webApp.getApplicationName().getValue())
                .cpuUsage(resourceUsage.getCpuUsage())
                .memoryUsage(resourceUsage.getMemoryUsage())
                .logs(logs)
                .build();
    }
}
