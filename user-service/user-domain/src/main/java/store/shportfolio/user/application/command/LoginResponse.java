package store.shportfolio.user.application.command;

public class LoginResponse {

    private final String userId;
    private final String email;
    private final String token;

    public LoginResponse(String userId, String email, String token) {
        this.userId = userId;
        this.email = email;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        private String userId;
        private String email;
        private String token;
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        public LoginResponse build() {
            return new LoginResponse(userId, email, token);
        }

    }

    public static LoginResponse.Builder builder() {
        return new LoginResponse.Builder();
    }
}
