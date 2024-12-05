package store.shportfolio.user.application;

import com.auth0.jwt.JWT;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtHandler {


    private final Environment env;

    public JwtHandler(Environment env) {
        this.env = env;
    }

    public String getEmailByToken(String token) {
        return null;
    }

    public String createToken() {
        return null;
    }

    public UUID getUserIdByToken(String token) {
        return null;
    }
}
