package store.shportfolio.user.domain.valueobject;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Password {

    private final String value;

    public Password(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.value);
    }

    public boolean isEncrypted() {
        return value != null && value.matches("^\\$2[ayb]\\$.{56}$");
    }
}
