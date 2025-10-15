package store.shportfolio.deploy.application;

import jakarta.validation.Valid;
import org.springframework.scheduling.annotation.Async;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.deploy.application.command.*;

import java.util.List;


public interface DeployApplicationService {


    WebAppCreateResponse createWebApp(@Valid UserGlobal userGlobal,
                                      @Valid WebAppCreateCommand webAppCreateCommand);


    void saveJarFile(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal);

    void reDeployJarFile(WebAppFileCreateCommand webAppFileCreateCommand, UserGlobal userGlobal);

    WebAppTrackResponse trackQueryWebApp(@Valid WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal);

    List<WebAppTrackResponse> trackQueryAllWebApps(UserGlobal userGlobal);

    void startContainer(@Valid WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal);

    void stopContainer(@Valid WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal);

    void deleteWebApp(@Valid WebAppDeleteCommand webAppDeleteCommand, UserGlobal userGlobal);

    void deleteAllWebApps(UserGlobal userGlobal);

    WebAppContainerResponse trackQueryDockerContainerResponse(@Valid WebAppTrackQuery webAppTrackQuery, UserGlobal userGlobal);

}
