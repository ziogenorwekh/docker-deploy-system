package store.shportfolio.deploy.infrastructure.test;

import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import store.shportfolio.deploy.application.ports.output.repository.DockerContainerRepository;
import store.shportfolio.deploy.infrastructure.jpa.adapter.DockerContainerRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.adapter.StorageRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.adapter.WebAppRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;
import store.shportfolio.deploy.infrastructure.jpa.mapper.DeployDataAccessMapper;
import store.shportfolio.deploy.infrastructure.jpa.repository.DockerContainerJpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.repository.StorageJpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.repository.WebAppJpaRepository;

@Configuration
@EntityScan(basePackageClasses = {DockerContainerEntity.class, WebAppEntity.class, StorageEntity.class})
@EnableJpaRepositories(basePackages = "store.shportfolio.deploy.infrastructure")
public class TestJpaConfig {

    @Bean
    public StorageRepositoryImpl storageRepository(StorageJpaRepository storageJpaRepository
    , EntityManager entityManager) {
        return new StorageRepositoryImpl(storageJpaRepository, deployDataAccessMapper(),entityManager);
    }

    @Bean
    public WebAppRepositoryImpl webAppRepository(WebAppJpaRepository webAppJpaRepository,
                                                 DockerContainerJpaRepository dockerContainerJpaRepository
            , StorageJpaRepository storageJpaRepository) {
        return new WebAppRepositoryImpl(deployDataAccessMapper(), webAppJpaRepository);
    }

    @Bean
    public DockerContainerRepositoryImpl dockerContainerRepository(
            DockerContainerJpaRepository dockerContainerJpaRepository, EntityManager entityManager) {
        return new DockerContainerRepositoryImpl(dockerContainerJpaRepository, deployDataAccessMapper()
        , entityManager);
    }

    @Bean
    public DeployDataAccessMapper deployDataAccessMapper() {
        return new DeployDataAccessMapper();
    }
}
