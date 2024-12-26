package store.shportfolio.deploy.application.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.command.WebAppContainerResponse;
import store.shportfolio.deploy.application.command.WebAppCreateResponse;
import store.shportfolio.deploy.application.command.WebAppTrackResponse;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.domain.entity.WebApp;

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

    public WebAppTrackResponse webAppToWebAppTrackResponse(WebApp webApp, String endpointUrl) {
        return WebAppTrackResponse.builder()
                .applicationId(webApp.getId().getValue())
                .applicationName(webApp.getApplicationName().getValue())
                .javaVersion(webApp.getJavaVersion().getVersion())
                .serverPort(webApp.getServerPort().getValue())
                .applicationStatus(webApp.getApplicationStatus())
                .endPointUrl(endpointUrl)
                .errorMessages(webApp.getErrorMessages())
                .userId(webApp.getUserId().getValue())
                .createdAt(webApp.getCreatedAt())
                .build();
    }

    public WebAppContainerResponse webAppToWebAppContainerResponse(WebApp webApp,
                                                                   ResourceUsage resourceUsage, String logs) {
        return WebAppContainerResponse.builder()
                .applicationId(webApp.getId().getValue().toString())
                .applicationName(webApp.getApplicationName().getValue())
                .cpuUsage(resourceUsage.getCpuUsage())
                .memoryUsage(resourceUsage.getMemoryUsage())
                .logs(logs)
                .build();
    }
}
