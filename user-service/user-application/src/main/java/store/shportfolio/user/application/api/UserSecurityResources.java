package store.shportfolio.user.application.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.user.application.UserAuthenticationService;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.GoogleException;
import store.shportfolio.user.domain.entity.User;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserSecurityResources {

    @Value("spring.security.oauth2.client.registration.google.client-id")
    private String CLIENT_ID;
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

    @RequestMapping(path = "/user/mail-send", method = RequestMethod.POST)
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
        return ResponseEntity.status(HttpStatus.OK)
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

    @RequestMapping(path = "/google", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> authenticateWithGoogle(@RequestBody Map<String, String> payload) {
        String tokenId = payload.get("tokenId");

        try {
            // Google ID 토큰 검증
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(CLIENT_ID)).build();

            GoogleIdToken idToken = verifier.verify(tokenId);
            if (idToken == null) {
                throw new GoogleException("Invalid Google token");
            }
            GoogleIdToken.Payload tokenPayload = idToken.getPayload();

            String email = tokenPayload.getEmail();
            String userId = (String) tokenPayload.get("sub");
            String name = (String) tokenPayload.get("name");
            Token token = userAuthenticationService.loginByGoogle(email, userId, name);

            LoginResponse loginResponse = LoginResponse.builder().email(email).userId(userId)
                    .token(token.getValue()).build();
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            throw new GoogleException(e.getMessage());
        }
    }
}
