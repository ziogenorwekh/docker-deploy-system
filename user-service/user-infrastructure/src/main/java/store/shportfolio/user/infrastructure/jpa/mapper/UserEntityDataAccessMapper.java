package store.shportfolio.user.infrastructure.jpa.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.Email;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.common.domain.valueobject.Username;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.valueobject.Password;
import store.shportfolio.user.infrastructure.jpa.entity.UserEntity;

@Component
public class UserEntityDataAccessMapper {


    public UserEntity userToUserEntity(User user) {
        return UserEntity.builder()
                .userId(user.getId().getValue().toString())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .password(user.getPassword().getValue())
                .oauth(user.getoAuth())
                .build();
    }

    public User userEntityToUser(UserEntity userEntity) {
        return new User(new UserId(userEntity.getUserId()),
                new Email(userEntity.getEmail()), new Username(userEntity.getUsername()),
                new Password(userEntity.getPassword()), userEntity.getAccountStatus(), userEntity.getCreatedAt(), userEntity.getOauth());
    }
}
