package store.shportfolio.user.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserAuthenticationDataDto {

    private final String userId;
    private final String email;
    private final String token;
    private final Boolean oauth;
}
