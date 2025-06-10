package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;

import java.util.Optional;

public interface StorageJpaRepository extends JpaRepository<StorageEntity, String> {

    Optional<StorageEntity> findByApplicationId(String applicationId);

    @Transactional
    @Modifying
    @Query("delete from StorageEntity s where s.applicationId = ?1")
    void removeByApplicationId(String applicationId);
}
