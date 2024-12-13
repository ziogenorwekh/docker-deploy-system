package store.shportfolio.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.UserAuthenticationService;
import store.shportfolio.user.application.command.*;

@RestController
@RequestMapping("/api/auth")
public class UserSecurityResources {

    private final UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserSecurityResources(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginCommand loginCommand) {

        LoginResponse loginResponse = userAuthenticationService.login(loginCommand);
        return ResponseEntity.ok(loginResponse);
    }

    @RequestMapping(path = "/user/mail-send" , method = RequestMethod.POST)
    public ResponseEntity<Void> sendEmail(@RequestBody EmailSendCommand emailSendCommand) {
        userAuthenticationService.sendEmail(emailSendCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/user/verify-mail", method = RequestMethod.POST)
    public ResponseEntity<EmailTemporalTokenResponse> verifyEmail(@RequestBody EmailVerificationCommand emailVerificationCommand) {
        Token token = userAuthenticationService.verifyEmail(emailVerificationCommand);
        EmailTemporalTokenResponse emailTemporalTokenResponse =
                EmailTemporalTokenResponse.builder()
                .token(token.getValue()).build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(emailTemporalTokenResponse);
    }

}
