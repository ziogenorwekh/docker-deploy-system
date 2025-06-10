package store.shportfolio.database.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "DATABASE_ENTITY")
@NoArgsConstructor
public class DatabaseEntity {


    @Id
    @Column(name = "DATABASE_ID", unique = true, nullable = false)
    private String databaseId;
    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;
    @Column(name = "DATABASE_NAME", nullable = false)
    private String databaseName;
    @Column(name = "DATABASE_USERNAME", nullable = false)
    private String databaseUsername;
    @Column(name = "DATABASE_PASSWORD", nullable = false)
    private String databasePassword;
    @Column(name = "DATABASE_ACCESS_URL", nullable = false)
    private String accessUrl;

    @Builder
    public DatabaseEntity(String databaseId, String userId,
                          String databaseName, String databaseUsername,
                          String databasePassword, String accessUrl) {
        this.databaseId = databaseId;
        this.userId = userId;
        this.databaseName = databaseName;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.accessUrl = accessUrl;
    }
}
