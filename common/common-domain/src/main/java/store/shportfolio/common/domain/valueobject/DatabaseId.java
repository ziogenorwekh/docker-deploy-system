package store.shportfolio.common.domain.valueobject;

import java.util.UUID;

public class DatabaseId extends BaseId<UUID> {
    public DatabaseId(UUID databaseId) {
        super(databaseId);
    }
}
