package store.shportfolio.user.usecase.ports.output.security;

import store.shportfolio.user.usecase.dto.UserAuthenticationDataDto;

public interface UserAuthenticatePort {

    UserAuthenticationDataDto authenticate(String email, String password);
}
