package store.shportfolio.deploy.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageInfo {

    private final String storageName;
    private final String fildUrl;

    @Builder
    public StorageInfo(String storageName, String fildUrl) {
        this.storageName = storageName;
        this.fildUrl = fildUrl;
    }
}
