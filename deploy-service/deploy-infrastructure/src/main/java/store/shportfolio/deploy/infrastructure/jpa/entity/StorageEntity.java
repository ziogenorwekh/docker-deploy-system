package store.shportfolio.deploy.infrastructure.jpa.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@ToString
@Table(name = "STORAGE_ENTITY")
@NoArgsConstructor
public class StorageEntity {

    @Id
    @Column(name = "APPLICATION_ID",unique = true, nullable = false)
    private String applicationId;

    @Column(name = "STORAGE_URL",unique = true)
    private String storageUrl;

    @Column(name = "STORAGE_NAME")
    private String storageName;

    @Builder
    public StorageEntity(String applicationId, String storageUrl, String storageName) {
        this.applicationId = applicationId;
        this.storageUrl = storageUrl;
        this.storageName = storageName;
    }
}
