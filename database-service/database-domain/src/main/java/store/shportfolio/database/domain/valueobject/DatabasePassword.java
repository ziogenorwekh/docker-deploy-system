package store.shportfolio.database.domain.valueobject;

import java.util.Objects;

public class DatabasePassword {
    private String value;

    public DatabasePassword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public boolean isValid() {
        return value != null && value.matches("^[a-zA-Z0-9]+$");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DatabasePassword password = (DatabasePassword) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
