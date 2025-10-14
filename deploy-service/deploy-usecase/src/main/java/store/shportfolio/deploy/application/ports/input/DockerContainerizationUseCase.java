package store.shportfolio.deploy.application.ports.input;

import org.springframework.web.multipart.MultipartFile;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

import java.io.File;

public interface DockerContainerizationUseCase {

    void uploadWebAppFile(WebApp webApp, File file);

}
