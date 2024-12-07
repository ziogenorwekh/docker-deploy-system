package store.shportfolio.common.domain.valueobject;

import java.util.Objects;

public class ServerPort {

    private final int port;

    public ServerPort(int port) {
        this.port = port;
    }

    public boolean isValid() {
        return port >= 10000 && port <= 65535;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServerPort that = (ServerPort) o;
        return port == that.port;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(port);
    }
}
