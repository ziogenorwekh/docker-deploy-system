package store.shportfolio.deploy.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.shportfolio.deploy.infrastructure.jpa.entity.WebAppEntity;

import java.util.List;
import java.util.Optional;

public interface WebAppJpaRepository extends JpaRepository<WebAppEntity, String> {

    Optional<WebAppEntity> findByApplicationId(String applicationId);

    Optional<WebAppEntity> findByApplicationName(String applicationName);


    @Query("select w from WebAppEntity w where w.userId = ?1")
    List<WebAppEntity> findAllByUserId(String userId);
}
