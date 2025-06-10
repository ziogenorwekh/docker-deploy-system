package store.shportfolio.deploy.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class StorageUrl {
    private final String value;

    public StorageUrl(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StorageUrl that = (StorageUrl) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
