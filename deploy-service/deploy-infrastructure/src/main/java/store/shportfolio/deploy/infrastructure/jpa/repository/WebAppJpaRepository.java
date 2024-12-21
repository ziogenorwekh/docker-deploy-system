package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;

import java.util.Optional;

public interface WebAppJpaRepository extends JpaRepository<WebAppEntity, String> {

    Optional<WebAppEntity> findByApplicationId(String applicationId);

    Optional<WebAppEntity> findByApplicationName(String applicationName);
}
