package store.shportfolio.user.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.shportfolio.user.infrastructure.jpa.entity.UserEntity;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    void deleteByUserId(String userId);
}
