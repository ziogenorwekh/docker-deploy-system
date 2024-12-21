package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;

import java.util.Optional;

public interface DockerContainerJpaRepository extends JpaRepository<DockerContainerEntity, String> {

    Optional<DockerContainerEntity> findByDockerContainerId(String dockerContainerId);

    Optional<DockerContainerEntity> findByApplicationId(String applicationId);
}
