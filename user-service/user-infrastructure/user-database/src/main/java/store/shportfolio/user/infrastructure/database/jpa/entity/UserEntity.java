package store.shportfolio.user.infrastructure.database.jpa.entity;

import jakarta.persistence.*;
import store.shportfolio.user.domain.valueobject.AccountStatus;

import java.time.LocalDateTime;


@Entity
@Table(name = "USER_ENTITY")
public class UserEntity {

    @Id
    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, name = "USERNAME", unique = true)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(nullable = false, name = "EMAIL", unique = true, updatable = false)
    private String email;

    @Column(name = "OAUTH",nullable = false)
    private Boolean oauth;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "ACCOUT_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    public UserEntity() {
    }

    public UserEntity(String userId, String username, String password, String email, Boolean oauth,
                      LocalDateTime createdAt, AccountStatus accountStatus) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.oauth = oauth;
        this.createdAt = createdAt;
        this.accountStatus = accountStatus;
    }


    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public Boolean getOauth() {
        return oauth;
    }

    public static class Builder {
        private String userId;
        private String username;
        private String password;
        private String email;
        private LocalDateTime createdAt;
        private AccountStatus accountStatus;
        private Boolean oauth;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
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

        public Builder accountStatus(AccountStatus accountStatus) {
            this.accountStatus = accountStatus;
            return this;
        }

        public Builder oauth(Boolean oauth) {
            this.oauth = oauth;
            return this;
        }

        public UserEntity build() {
            return new UserEntity(userId, username, password, email, oauth, createdAt, accountStatus);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
