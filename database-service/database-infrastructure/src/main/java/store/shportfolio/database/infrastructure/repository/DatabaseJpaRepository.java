package store.shportfolio.database.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;

import java.util.Optional;

public interface DatabaseJpaRepository extends JpaRepository<DatabaseEntity, String> {

    Optional<DatabaseEntity> findByUserId(String userId);
}
