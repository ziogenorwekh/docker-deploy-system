package store.shportfolio.user.application.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.user.application.UserAuthenticationService;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.domain.entity.User;

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

    @RequestMapping(path = "/user/info", method = RequestMethod.GET)
    public ResponseEntity<UserGlobal> getUserInfo(@RequestHeader("Authorization") String token) {
        Token tokenVO = new Token(token);
        User user = userAuthenticationService.getUserByToken(tokenVO);
        return ResponseEntity.ok(UserGlobal.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername().getValue()).build());
    }

}
