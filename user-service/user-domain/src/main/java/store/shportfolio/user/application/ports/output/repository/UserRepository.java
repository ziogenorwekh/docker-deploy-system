package store.shportfolio.user.application.ports.output.repository;

import store.shportfolio.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(UUID userId);
}
