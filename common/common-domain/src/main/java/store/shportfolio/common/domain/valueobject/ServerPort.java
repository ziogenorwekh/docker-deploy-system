package store.shportfolio.common.domain.valueobject;

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
}
