package store.shportfolio.deploy.application.ports.input;

import store.shportfolio.deploy.domain.entity.WebApp;

import java.io.File;

public interface DockerContainerizationUseCase {

    void uploadWebAppFile(WebApp webApp, File file);

    void reUploadWebAppFile(WebApp webApp, File file);
}
