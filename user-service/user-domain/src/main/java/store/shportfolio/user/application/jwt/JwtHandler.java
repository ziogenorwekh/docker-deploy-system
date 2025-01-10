package store.shportfolio.user.application.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.exception.TokenInvalidException;

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
        long expirationMinutes = Long.parseLong(Objects.requireNonNull(tokenExpirationTime));
        String secret = env.getProperty("server.token.secret");
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime expirationTime = now.plusMinutes(expirationMinutes);
        String emailToken = JWT.create().withIssuer(email).withExpiresAt(expirationTime.toInstant())
                .sign(Algorithm.HMAC256(secret));
        log.info("Successfully created the temporary email token -> {}", email);
        return new Token(emailToken);
    }

    public Token createLoginToken(String email, String userId) {
        String tokenExpirationTime = env.getProperty("server.token.login.expiration");
        long expirationMinutes = Long.parseLong(Objects.requireNonNull(tokenExpirationTime));
        String secret = env.getProperty("server.token.secret");
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime expirationTime = now.plusMinutes(expirationMinutes);
        String loginToken = JWT.create().withIssuer(userId)
                .withSubject(email).withExpiresAt(expirationTime.toInstant())
                .sign(Algorithm.HMAC256(secret));
        log.info("Successfully created the login token -> {}", email);
        return new Token(loginToken);
    }

    public String getEmailFromToken(Token token) {
        log.info("Get email from token -> {}", token.getValue());
        return extractClaimFromToken(token, "email");
    }

    public String getUserIdByToken(Token token) {
        log.info("Get user ID from token -> {}", token.getValue());
        return extractClaimFromToken(token, "userId");
    }

    public String getUserIdFromToken(String userId,String token) {
        String claimFromToken = extractClaimFromToken(new Token(token), "userId");
        if (!userId.equals(claimFromToken)) {
            throw new TokenInvalidException("is not matching user ID");
        }
        return claimFromToken;
    }

    private String extractClaimFromToken(Token token, String claimType) {
        String tokenValue = token.getValue().substring(7);
        String secret = env.getProperty("server.token.secret");

        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(tokenValue).getIssuer();
        } catch (TokenExpiredException e) {
            log.error("{} token has expired: {}", claimType, e.getMessage());
            throw new TokenInvalidException("The token has expired. Please login again.");
        } catch (JWTDecodeException e) {
            log.error("Failed to decode the {} token: {}", claimType, e.getMessage());
            throw new TokenInvalidException("The token is invalid. Please check your token.");
        } catch (JWTVerificationException e) {
            log.error("{} token verification failed: {}", claimType, e.getMessage());
            throw new TokenInvalidException("Token verification failed. Please login again.");
        } catch (Exception e) {
            log.error("Unexpected error while verifying {} token: {}", claimType, e.getMessage());
            throw new TokenInvalidException("An unexpected error occurred while processing the token.");
        }
    }

}
