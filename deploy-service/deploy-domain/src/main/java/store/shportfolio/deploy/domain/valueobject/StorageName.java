package store.shportfolio.deploy.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class StorageName {
    private final String value;

    public StorageName(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StorageName that = (StorageName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
