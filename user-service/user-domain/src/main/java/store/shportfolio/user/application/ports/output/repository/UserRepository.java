package store.shportfolio.user.application.ports.output.repository;

import store.shportfolio.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(String userId);


    Optional<User> findByEmail(String email);

    void remove(String userId);

    Optional<User> findByUsername(String username);
}
