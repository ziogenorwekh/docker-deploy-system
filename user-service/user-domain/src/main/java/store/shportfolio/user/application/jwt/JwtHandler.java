package store.shportfolio.user.application.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.Token;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class JwtHandler {


    private final Environment env;

    public JwtHandler(Environment env) {
        this.env = env;
    }

    public Token createSignupTemporaryToken(String email) {
        String tokenExpirationTime = env.getProperty("server.token.email.expiration");
        String secret = env.getProperty("server.token.secret");
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime expirationTime = now.
                plusMinutes(Long.parseLong(Objects
                        .requireNonNull(tokenExpirationTime)));
        String emailToken = JWT.create().withIssuer(email).withExpiresAt(expirationTime.toInstant())
                .sign(Algorithm.HMAC256(secret));
        log.info("Successfully created the temporary email token -> {}", email);
        return new Token(emailToken);
    }

    public Token createLoginToken(String email, String userId) {
        String tokenExpirationTime = env.getProperty("server.token.login.expiration");
        String secret = env.getProperty("server.token.secret");
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime expirationTime = now.
                plusMinutes(Long.parseLong(Objects
                        .requireNonNull(tokenExpirationTime)));
        String loginToken = JWT.create().withIssuer(userId)
                .withSubject(email).withExpiresAt(expirationTime.toInstant())
                .sign(Algorithm.HMAC256(secret));
        log.info("Successfully created the login token -> {}", email);
        return new Token(loginToken);
    }

    public String getEmailFromToken(Token token) {
        String secret = env.getProperty("server.token.secret");
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token.getValue()).getIssuer();
    }

    public String getUserIdByToken(Token token) {
        String secret = env.getProperty("server.token.secret");
        String userId = JWT.require(Algorithm.HMAC256(secret)).build().verify(token.getValue()).getIssuer();
        return userId;
    }
}
