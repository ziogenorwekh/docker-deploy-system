package store.shportfolio.deploy.domain.valueobject;

import java.util.Objects;

public class DockerContainerId {

    private final String value;

    public DockerContainerId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DockerContainerId that = (DockerContainerId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
