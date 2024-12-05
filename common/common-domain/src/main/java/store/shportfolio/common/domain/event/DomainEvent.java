package store.shportfolio.common.domain.event;

public class DomainEvent<Value> {

    private final Value entity;

    public DomainEvent(Value entity) {
        this.entity = entity;
    }

    public Value getEntity() {
        return entity;
    }
}
