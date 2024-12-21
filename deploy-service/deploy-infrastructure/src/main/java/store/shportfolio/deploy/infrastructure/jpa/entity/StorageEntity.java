package store.shportfolio.deploy.infrastructure.jpa.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class StorageEntity {

    @Id
    private String applicationId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "applicationId")
    private WebAppEntity webAppEntity;

    private String storageUrl;

    private String storageName;

    @Builder
    public StorageEntity(String applicationId, WebAppEntity webAppEntity, String storageUrl, String storageName) {
        this.applicationId = applicationId;
        this.webAppEntity = webAppEntity;
        this.storageUrl = storageUrl;
        this.storageName = storageName;
    }
}
