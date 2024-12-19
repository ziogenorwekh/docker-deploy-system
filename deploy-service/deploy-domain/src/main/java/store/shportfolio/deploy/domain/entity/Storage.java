package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.common.domain.entitiy.BaseEntity;
import store.shportfolio.common.domain.valueobject.ApplicationId;

@Getter
public class Storage extends BaseEntity<ApplicationId> {

    private String storageUrl;
    private String storageName;

    public Storage(ApplicationId applicationId) {
        super.setId(applicationId);
    }

    @Builder
    public Storage(ApplicationId applicationId, String storageUrl,
                   String storageName) {
        this.storageName = storageName;
        super.setId(applicationId);
        this.storageUrl = storageUrl;
    }

    public static Storage createStorage(ApplicationId applicationId) {
        return Storage.builder().
                applicationId(applicationId)
                .storageUrl("")
                .storageName("")
                .build();
    }

    public void savedStorage(String storageUrl, String storageName) {
        this.storageUrl = storageUrl;
        this.storageName = storageName;
    }
}
