package store.shportfolio.user.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserTrackResponse {

    private final UUID userId;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;


    public UserTrackResponse(UUID userId, String username, String email, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class Builder {

        private UUID userId;
        private String username;
        private String email;
        private LocalDateTime createdAt;

        // 빌더 메서드
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // 빌더 메서드를 이용해 객체 생성
        public UserTrackResponse build() {
            return new UserTrackResponse(userId, username, email, createdAt);
        }
    }

    // 빌더 패턴을 사용할 수 있도록 static 메서드 추가
    public static UserTrackResponse.Builder builder() {
        return new Builder();
    }


}
