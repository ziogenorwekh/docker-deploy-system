package store.shportfolio.deploy.infrastructure.jpa.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.shportfolio.deploy.application.output.repository.WebAppRepository;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;
import store.shportfolio.deploy.infrastructure.jpa.mapper.DeployDataAccessMapper;
import store.shportfolio.deploy.infrastructure.jpa.repository.WebAppJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class WebAppRepositoryImpl implements WebAppRepository {

    private final DeployDataAccessMapper deployDataAccessMapper;
    private final WebAppJpaRepository webAppJpaRepository;

    @Autowired
    public WebAppRepositoryImpl(DeployDataAccessMapper deployDataAccessMapper,
                                WebAppJpaRepository webAppJpaRepository) {
        this.deployDataAccessMapper = deployDataAccessMapper;
        this.webAppJpaRepository = webAppJpaRepository;
    }

    @Override
    public WebApp save(WebApp webApp) {
        log.debug("WebApp: {}", webApp);
        WebAppEntity webAppEntity = deployDataAccessMapper
                .webAppEntityToWebAppEntity(webApp);
        WebAppEntity saved = webAppJpaRepository.save(webAppEntity);
        log.debug("result -> {}", saved.toString());
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
    public Optional<WebApp> findByPort(int port) {
        return webAppJpaRepository.findByServerPort(port).map(deployDataAccessMapper::webAppEntityToWebAppEntity);
    }

    @Override
    public Optional<WebApp> findByApplicationName(String applicationName) {
        return webAppJpaRepository.findByApplicationName(applicationName)
                .map(deployDataAccessMapper::webAppEntityToWebAppEntity);
    }

    @Override
    public List<WebApp> findAll(String userId) {
        List<WebAppEntity> allByUserId = webAppJpaRepository.findAllByUserId(userId);
        return allByUserId.stream().map(deployDataAccessMapper::webAppEntityToWebAppEntity)
                .collect(Collectors.toList());
    }
}
