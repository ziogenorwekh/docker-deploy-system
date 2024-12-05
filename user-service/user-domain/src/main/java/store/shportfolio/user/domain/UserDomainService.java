package store.shportfolio.user.domain;

import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;

import java.util.UUID;


public interface UserDomainService {

    User createUser(UUID userId, String email, String newUsername, String newPassword);
    void updateUser(User user, String currentPassword, String newPassword);
    void disableUser(User user);
    UserDeleteEvent deleteUser(User user);

}
