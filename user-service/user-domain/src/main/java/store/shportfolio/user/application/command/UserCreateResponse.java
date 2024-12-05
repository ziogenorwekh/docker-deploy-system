package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserCreateResponse {

    private final UUID userId;
    private final String username;
    private final String email;

    public UserCreateResponse(UUID userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private UUID userId;
        private String username;
        private String email;
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public UserCreateResponse build() {
            return new UserCreateResponse(userId, username, email);
        }
    }
    public static UserCreateResponse.Builder builder() {
        return new UserCreateResponse.Builder();
    }

}
