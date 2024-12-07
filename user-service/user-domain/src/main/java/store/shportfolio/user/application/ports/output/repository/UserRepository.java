package store.shportfolio.user.application.ports.output.repository;

import org.springframework.stereotype.Repository;
import store.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(String userId);


    Optional<User> findByEmail(String email);

    void remove(String userId);

    Optional<User> findByUsername(String username);
}
