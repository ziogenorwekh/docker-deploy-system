package store.shportfolio.user.application.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.user.usecase.UserAuthenticationUseCaseImpl;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.exception.GoogleException;
import store.shportfolio.user.domain.entity.User;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class UserSecurityResources {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    private final UserAuthenticationUseCaseImpl usercaseImplAuthenticationUse;

    @Autowired
    public UserSecurityResources(UserAuthenticationUseCaseImpl usercaseImplAuthenticationUse) {
        this.usercaseImplAuthenticationUse = usercaseImplAuthenticationUse;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginCommand loginCommand) {

        LoginResponse loginResponse = usercaseImplAuthenticationUse.login(loginCommand);
        return ResponseEntity.ok(loginResponse);
    }

    @RequestMapping(path = "/user/mail-send", method = RequestMethod.POST)
    public ResponseEntity<Void> sendEmail(@RequestBody EmailSendCommand emailSendCommand) {
        log.info("Sending email to {}", emailSendCommand.getEmail());
        usercaseImplAuthenticationUse.sendEmail(emailSendCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/user/verify-mail", method = RequestMethod.POST)
    public ResponseEntity<EmailTemporalTokenResponse> verifyEmail(@RequestBody EmailVerificationCommand emailVerificationCommand) {
        Token token = usercaseImplAuthenticationUse.verifyEmail(emailVerificationCommand);
        EmailTemporalTokenResponse emailTemporalTokenResponse =
                EmailTemporalTokenResponse.builder()
                        .token(token.getValue()).build();
        return ResponseEntity.status(HttpStatus.OK)
                .body(emailTemporalTokenResponse);
    }

    @RequestMapping(path = "/user/info", method = RequestMethod.GET)
    public ResponseEntity<UserGlobal> getUserInfo(@RequestHeader("Authorization") String token) {
        Token tokenVO = new Token(token);
        User user = usercaseImplAuthenticationUse.getUserByToken(tokenVO);
        log.info("user found: email {}", user.getEmail().getValue());
        return ResponseEntity.ok(UserGlobal.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername().getValue()).build());
    }

    @RequestMapping(path = "/google", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> authenticateWithGoogle(
            @RequestBody Map<String, String> payload) {
        String tokenId = payload.get("tokenId");
        log.info("tokenId: {}", tokenId);
        try {

            // Google ID 토큰 검증
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory()
            ).setAudience(Collections.singletonList(CLIENT_ID)).build();

            GoogleIdToken idToken = verifier.verify(tokenId);
            if (idToken == null) {
                log.error("Google ID token verification failed");
                throw new GoogleException("Invalid Google token");
            }
            GoogleIdToken.Payload tokenPayload = idToken.getPayload();
            String email = tokenPayload.getEmail();
            log.info("email: {}", email);
            String userId = (String) tokenPayload.getSubject();
            String name = (String) tokenPayload.get("name");
            Token token = usercaseImplAuthenticationUse.loginByGoogle(email, userId, name);

            LoginResponse loginResponse = LoginResponse.builder().email(email).userId(userId)
                    .token(token.getValue()).oauth(true).build();
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            throw new GoogleException(e.getMessage());
        }
    }
}
