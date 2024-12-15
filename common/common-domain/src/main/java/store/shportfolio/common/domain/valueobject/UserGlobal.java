package store.shportfolio.common.domain.valueobject;

public class UserGlobal {

    private final String userId;
    private final String username;

    public UserGlobal(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public static class Builder {
        private String userId;
        private String username;
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public UserGlobal build() {
            return new UserGlobal(userId, username);
        }
    }

    public static UserGlobal.Builder builder() {
        return new UserGlobal.Builder();
    }
}
