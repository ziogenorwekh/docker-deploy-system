package store.shportfolio.user.domain;

import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;


public class UserDomainServiceImpl implements UserDomainService {

    @Override
    public User createUser(String userId, String email, String newUsername, String newPassword) {
        return User.createUser(userId, email, newUsername, newPassword, false);
    }

    @Override
    public User createGoogleUser(String googleId, String email, String username) {
        return User.createGoogleUser(googleId, email, username, true);
    }

    @Override
    public User updateUser(User user, String newPassword) {
        user.updatePassword(newPassword);
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
