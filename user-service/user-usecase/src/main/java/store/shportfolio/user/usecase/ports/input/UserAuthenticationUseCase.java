package store.shportfolio.user.usecase.ports.input;

import jakarta.validation.Valid;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.usecase.command.EmailSendCommand;
import store.shportfolio.user.usecase.command.EmailVerificationCommand;
import store.shportfolio.user.usecase.command.LoginCommand;
import store.shportfolio.user.usecase.command.LoginResponse;

public interface UserAuthenticationUseCase {

    LoginResponse login(@Valid LoginCommand loginCommand);

    Token loginByGoogle( String email, String userId, String username);

    User getUserByToken(@Valid Token token);

    void sendEmail(@Valid EmailSendCommand emailSendCommand);

    Token verifyEmail(@Valid EmailVerificationCommand emailVerificationCommand);
}
