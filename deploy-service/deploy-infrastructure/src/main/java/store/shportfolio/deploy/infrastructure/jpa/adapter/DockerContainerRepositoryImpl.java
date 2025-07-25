package store.shportfolio.deploy.infrastructure.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.shportfolio.deploy.application.output.repository.DockerContainerRepository;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;
import store.shportfolio.deploy.infrastructure.jpa.mapper.DeployDataAccessMapper;
import store.shportfolio.deploy.infrastructure.jpa.repository.DockerContainerJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DockerContainerRepositoryImpl implements DockerContainerRepository {

    private final DockerContainerJpaRepository dockerContainerJpaRepository;
    private final DeployDataAccessMapper deployDataAccessMapper;
    @Autowired
    public DockerContainerRepositoryImpl(DockerContainerJpaRepository dockerContainerJpaRepository,
                                         DeployDataAccessMapper deployDataAccessMapper) {
        this.dockerContainerJpaRepository = dockerContainerJpaRepository;
        this.deployDataAccessMapper = deployDataAccessMapper;
    }

    @Override
    public DockerContainer save(DockerContainer dockerContainer) {

        DockerContainerEntity dockerContainerEntity = deployDataAccessMapper.
                dockerContainerToDockerContainerEntity(dockerContainer);
        DockerContainerEntity saved = dockerContainerJpaRepository.save(dockerContainerEntity);
        return deployDataAccessMapper.dockerContainerEntityToDockerContainer(saved);
    }

    @Override
    public Optional<DockerContainer> findByDockerContainerId(String dockerContainerId) {
        return dockerContainerJpaRepository.findByDockerContainerId(dockerContainerId)
                .map(deployDataAccessMapper::dockerContainerEntityToDockerContainer);
    }

    @Override
    public Optional<DockerContainer> findByApplicationId(UUID applicationId) {

        return dockerContainerJpaRepository.findByApplicationId(applicationId.toString())
                .map(deployDataAccessMapper::dockerContainerEntityToDockerContainer);
    }

    @Override
    public void remove(DockerContainer dockerContainer) {
        dockerContainerJpaRepository.findByApplicationId(dockerContainer.getId().getValue().toString())
                .ifPresent(dockerContainerJpaRepository::delete);
    }

    @Override
    public void removeByApplicationId(UUID applicationId) {
        dockerContainerJpaRepository.removeByApplicationId(applicationId.toString());
    }
}
