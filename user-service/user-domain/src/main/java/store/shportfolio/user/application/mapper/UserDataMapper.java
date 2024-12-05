package store.shportfolio.user.application.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.user.application.command.UserCreateResponse;
import store.shportfolio.user.application.command.UserTrackResponse;
import store.shportfolio.user.domain.entity.User;

@Component
public class UserDataMapper {


    public UserTrackResponse toUserTrackResponse(User user) {
        return UserTrackResponse.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .createdAt(user.getCreatedAt()).build();
    }

    public UserCreateResponse toUserCreateResponse(User user) {
        return UserCreateResponse.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .build();
    }
}
