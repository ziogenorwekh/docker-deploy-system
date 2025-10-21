package store.shportfolio.deploy.application.ports.output.repository;

import store.shportfolio.deploy.domain.entity.DockerContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DockerContainerRepository {


    DockerContainer save(DockerContainer dockerContainer);

    Optional<DockerContainer> findByDockerContainerId(String dockerContainerId);
    Optional<DockerContainer> findByApplicationId(UUID applicationId);

    void remove(DockerContainer dockerContainer);

    List<DockerContainer> findAll();

    void removeByApplicationId(UUID applicationId);

    void flush();

    void clear();
}
