package store.shportfolio.common.domain.valueobject;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserGlobal that = (UserGlobal) o;
        return Objects.equals(userId, that.userId) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }
}
