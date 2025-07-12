package store.shportfolio.user.usecase.command;

public class EmailTemporalTokenResponse {

    private String token;

    public EmailTemporalTokenResponse() {

    }

    public EmailTemporalTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        private String token;
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        public EmailTemporalTokenResponse build() {
            return new EmailTemporalTokenResponse(token);
        }
    }
    public static EmailTemporalTokenResponse.Builder builder() {
        return new EmailTemporalTokenResponse.Builder();
    }
}
