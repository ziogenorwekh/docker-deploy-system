package store.shportfolio.deploy.application.output.repository;

import store.shportfolio.deploy.domain.entity.DockerContainer;

import java.util.Optional;
import java.util.UUID;

public interface DockerContainerRepository {


    DockerContainer save(DockerContainer dockerContainer);

    Optional<DockerContainer> findByDockerContainerId(String dockerContainerId);
    Optional<DockerContainer> findByApplicationId(UUID applicationId);

    void remove(DockerContainer dockerContainer);

    void removeByApplicationId(UUID applicationId);
}
