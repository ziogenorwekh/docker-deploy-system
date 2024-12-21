package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;

import java.util.Optional;

public interface StorageJpaRepository extends JpaRepository<StorageEntity, String> {

    Optional<StorageEntity> findByApplicationId(String applicationId);
}
