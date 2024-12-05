package store.shportfolio.common.domain.valueobject;

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
}
