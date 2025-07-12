package store.shportfolio.user.usecase.command;

public class LoginResponse {

    private final String userId;
    private final String email;
    private final String token;
    private final Boolean oauth;

    public LoginResponse(String userId, String email, String token, Boolean oauth) {
        this.userId = userId;
        this.email = email;
        this.token = token;
        this.oauth = oauth;
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

    public Boolean getOauth() {
        return oauth;
    }

    public static class Builder {
        private String userId;
        private String email;
        private String token;
        private Boolean oauth;

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

        public Builder oauth(Boolean oauth) {
            this.oauth = oauth;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(userId, email, token, oauth);
        }

    }

    public static LoginResponse.Builder builder() {
        return new LoginResponse.Builder();
    }
}
