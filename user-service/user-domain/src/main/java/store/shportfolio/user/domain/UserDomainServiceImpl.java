package store.shportfolio.user.domain;

import lombok.extern.slf4j.Slf4j;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;

import java.util.UUID;


@Slf4j
public class UserDomainServiceImpl implements UserDomainService {

    @Override
    public User createUser(String userId, String email, String newUsername, String newPassword) {
        log.info("Creating user with email {}", email);
        return User.createUser(userId, email, newUsername, newPassword);
    }

    @Override
    public User updateUser(User user, String currentPassword, String newPassword) {
        log.info("Updating user with email {}", user.getEmail());
        user.updatePassword(currentPassword, newPassword);
        return user;
    }

    @Override
    public void disableUser(User user) {
        log.info("Disabling user with email {}", user.getEmail());
        user.disableAccount();
    }

    @Override
    public UserDeleteEvent deleteUser(User user) {
        log.info("Deleting user with email {}", user.getEmail());
        user.disableAccount();
        return new UserDeleteEvent(user);
    }

}
