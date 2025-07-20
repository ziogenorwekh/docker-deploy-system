package store.shportfolio.deploy.application.output.repository;

import store.shportfolio.deploy.domain.entity.WebApp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebAppRepository {


    WebApp save(WebApp webApp);

    Optional<WebApp> findByApplicationId(UUID applicationId);

    Optional<WebApp> findByDockerContainerId(String dockerContainerId);

    Optional<WebApp> findByStorageId(String storageId);

    void deleteByApplicationId(UUID applicationId);

    Optional<WebApp> findByPort(int port);


    Optional<WebApp> findByApplicationName(String applicationName);

    List<WebApp> findAll(String userId);
}
