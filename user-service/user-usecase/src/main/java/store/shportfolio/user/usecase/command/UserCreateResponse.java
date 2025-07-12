package store.shportfolio.user.usecase.command;

public class UserCreateResponse {

    private final String userId;
    private final String username;
    private final String email;

    public UserCreateResponse(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String userId;
        private String username;
        private String email;
        public Builder userId(String userId) {
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
