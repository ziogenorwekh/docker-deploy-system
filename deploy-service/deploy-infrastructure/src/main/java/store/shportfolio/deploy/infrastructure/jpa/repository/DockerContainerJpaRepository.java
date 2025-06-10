package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import store.shportfolio.deploy.infrastructure.jpa.entity.DockerContainerEntity;

import java.util.Optional;

public interface DockerContainerJpaRepository extends JpaRepository<DockerContainerEntity, String> {

    Optional<DockerContainerEntity> findByDockerContainerId(String dockerContainerId);

    Optional<DockerContainerEntity> findByApplicationId(String applicationId);

    @Transactional
    @Modifying
    @Query("delete from DockerContainerEntity d where d.applicationId = ?1")
    void removeByApplicationId(String applicationId);
}
