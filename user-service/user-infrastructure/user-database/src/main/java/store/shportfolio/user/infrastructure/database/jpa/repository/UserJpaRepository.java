package store.shportfolio.user.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.shportfolio.user.infrastructure.database.jpa.entity.UserEntity;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    void deleteByUserId(String userId);
}
