package store.shportfolio.user.infrastructure.jpa.entity;

import jakarta.persistence.*;
import store.shportfolio.user.domain.valueobject.AccountStatus;

import java.time.LocalDateTime;


@Entity
@Table(name = "USER_ENTITY")
public class UserEntity {

    @Id
    @Column(nullable = false,unique = true)
    private String userId;

    @Column(nullable = false,name = "username", unique = true, updatable = false)
    private String username;

    @Column(nullable = false,name = "password")
    private String password;

    @Column(nullable = false,name = "email", unique = true, updatable = false)
    private String email;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "accountStatus")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    public UserEntity() {
    }

    public UserEntity(String userId, String username, String password, String email,
                      LocalDateTime createdAt, AccountStatus accountStatus) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
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

    public static class Builder {
        private String userId;
        private String username;
        private String password;
        private String email;
        private LocalDateTime createdAt;
        private AccountStatus accountStatus;
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
        public UserEntity build() {
            return new UserEntity(userId, username, password, email, createdAt, accountStatus);
        }
    }

    public static UserEntity.Builder builder() {
        return new Builder();
    }
}
