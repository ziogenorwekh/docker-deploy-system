package store.shportfolio.common.domain.valueobject;

import java.util.Objects;

public class ServerPort {

    private final int value;

    public ServerPort(int value) {
        this.value = value;
    }

    public boolean isValid() {
        return value >= 8000 && value <= 65535;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServerPort that = (ServerPort) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
