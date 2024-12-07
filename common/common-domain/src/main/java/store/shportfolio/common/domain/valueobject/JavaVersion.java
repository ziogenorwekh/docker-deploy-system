package store.shportfolio.common.domain.valueobject;

import java.util.Objects;

public class JavaVersion {

    private final int version;

    public JavaVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public boolean isValid() {
        return version >= 7;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JavaVersion that = (JavaVersion) o;
        return version == that.version;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }
}
