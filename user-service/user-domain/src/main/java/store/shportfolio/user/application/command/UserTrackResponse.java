package store.shportfolio.user.application.command;

import java.time.LocalDateTime;
import java.lang.String;

public class UserTrackResponse {

    private final String userId;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;


    public UserTrackResponse(String userId, String username, String email, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class Builder {

        private String userId;
        private String username;
        private String email;
        private LocalDateTime createdAt;

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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserTrackResponse build() {
            return new UserTrackResponse(userId, username, email, createdAt);
        }
    }

    public static UserTrackResponse.Builder builder() {
        return new Builder();
    }


}
