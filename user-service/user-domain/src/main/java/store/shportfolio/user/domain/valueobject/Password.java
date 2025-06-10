package store.shportfolio.user.domain.valueobject;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
