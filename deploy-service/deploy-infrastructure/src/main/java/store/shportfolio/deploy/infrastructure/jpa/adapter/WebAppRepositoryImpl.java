package store.shportfolio.deploy.infrastructure.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.shportfolio.deploy.application.ports.output.repository.WebAppRepository;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;
import store.shportfolio.deploy.infrastructure.jpa.mapper.DeployDataAccessMapper;
import store.shportfolio.deploy.infrastructure.jpa.repository.WebAppJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WebAppRepositoryImpl implements WebAppRepository {

    private final DeployDataAccessMapper deployDataAccessMapper;
    private final WebAppJpaRepository webAppJpaRepository;

    @Autowired
    public WebAppRepositoryImpl(DeployDataAccessMapper deployDataAccessMapper, WebAppJpaRepository webAppJpaRepository) {
        this.deployDataAccessMapper = deployDataAccessMapper;
        this.webAppJpaRepository = webAppJpaRepository;
    }

    @Override
    public WebApp save(WebApp webApp) {
        StorageEntity storageEntity = deployDataAccessMapper.storageEntityToStorage(webApp.getStorage());
        DockerContainerEntity dockerContainerEntity = deployDataAccessMapper
                .dockerContainerToDockerContainerEntity(webApp.getDockerContainer());
        WebAppEntity saved = deployDataAccessMapper
                .webAppEntityToWebAppEntity(webApp, dockerContainerEntity, storageEntity);
        return deployDataAccessMapper.webAppEntityToWebAppEntity(saved);
    }

    @Override
    public Optional<WebApp> findByApplicationId(UUID applicationId) {
        return webAppJpaRepository.findByApplicationId(applicationId.toString())
                .map(deployDataAccessMapper::webAppEntityToWebAppEntity);
    }

    @Override
    public Optional<WebApp> findByDockerContainerId(String dockerContainerId) {
        return Optional.empty();
    }

    @Override
    public Optional<WebApp> findByStorageId(String storageId) {
        return Optional.empty();
    }

    @Override
    public void deleteByApplicationId(UUID applicationId) {
        webAppJpaRepository.findByApplicationId(applicationId.toString())
                .ifPresent(webAppJpaRepository::delete);
    }

    @Override
    public Optional<WebApp> findByApplicationName(String applicationName) {
        return webAppJpaRepository.findByApplicationName(applicationName)
                .map(deployDataAccessMapper::webAppEntityToWebAppEntity);
    }
}
