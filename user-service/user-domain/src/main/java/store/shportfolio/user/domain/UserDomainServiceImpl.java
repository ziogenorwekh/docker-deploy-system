package store.shportfolio.user.domain;

import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;

import java.util.UUID;


public class UserDomainServiceImpl implements UserDomainService {

    @Override
    public User createUser(UUID userId, String email, String newUsername, String newPassword) {
        return User.createUser(userId, email, newUsername, newPassword);
    }

    @Override
    public User updateUser(User user, String currentPassword, String newPassword) {
        user.updatePassword(currentPassword, newPassword);
        return user;
    }

    @Override
    public void disableUser(User user) {
        user.disableAccount();
    }

    @Override
    public UserDeleteEvent deleteUser(User user) {
        user.disableAccount();
        return new UserDeleteEvent(user);
    }

}
