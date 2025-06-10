package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import store.shportfolio.common.domain.entitiy.BaseEntity;
import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.deploy.domain.valueobject.StorageName;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

@Getter
@ToString
public class Storage extends BaseEntity<ApplicationId> {

    private StorageUrl storageUrl;
    private StorageName storageName;

    public Storage(ApplicationId applicationId) {
        super.setId(applicationId);
    }

    @Builder
    public Storage(ApplicationId applicationId, String storageUrl,
                   String storageName) {
        this.storageName = new StorageName(storageName);
        super.setId(applicationId);
        this.storageUrl = new StorageUrl(storageUrl);
    }

    public static Storage createStorage(ApplicationId applicationId) {
        return Storage.builder().
                applicationId(applicationId)
                .storageUrl("")
                .storageName("")
                .build();
    }

    public void savedStorage(StorageUrl storageUrl, StorageName storageName) {
        this.storageUrl = storageUrl;
        this.storageName = storageName;
    }


}
