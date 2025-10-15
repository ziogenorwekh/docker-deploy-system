package store.shportfolio.database.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;

import java.util.List;
import java.util.Optional;

public interface DatabaseJpaRepository extends JpaRepository<DatabaseEntity, String> {

    Optional<DatabaseEntity> findByUserId(String userId);

    Optional<DatabaseEntity> findDatabaseEntityByDatabaseName(String databaseName);

    Optional<DatabaseEntity> findDatabaseEntityByUserIdAndDatabaseName(String userId, String databaseName);

    List<DatabaseEntity> findAllByUserId(String userId);

    void deleteAllByUserId(String userId);

}
