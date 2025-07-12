package store.shportfolio.user.usecase.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.user.usecase.command.LoginResponse;
import store.shportfolio.user.usecase.command.UserCreateResponse;
import store.shportfolio.user.usecase.command.UserTrackResponse;
import store.shportfolio.user.usecase.dto.UserAuthenticationDataDto;
import store.shportfolio.user.domain.entity.User;

@Component
public class UserDataMapper {


    public UserTrackResponse toUserTrackResponse(User user) {
        return UserTrackResponse.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .createdAt(user.getCreatedAt())
                .oAuth(user.getoAuth())
                .build();
    }

    public UserCreateResponse toUserCreateResponse(User user) {
        return UserCreateResponse.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .username(user.getUsername().getValue())
                .build();
    }

    public LoginResponse toLoginResponse(UserAuthenticationDataDto dto) {
        return LoginResponse.builder().userId(dto.getUserId())
                .email(dto.getEmail())
                .oauth(dto.getOauth())
                .token(dto.getToken()).build();
    }
}
