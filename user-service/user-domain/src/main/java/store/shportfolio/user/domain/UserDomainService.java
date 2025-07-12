package store.shportfolio.user.domain;

import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;

import java.util.UUID;


public interface UserDomainService {

    User createUser(String userId, String email, String newUsername, String newPassword);

    User createGoogleUser(String googleId,String email,String username);
    User updateUser(User user, String newPassword);
    void disableUser(User user);
    UserDeleteEvent deleteUser(User user);

}
