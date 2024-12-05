package store.shportfolio.user.domain.event;

import store.shportfolio.common.domain.event.DomainEvent;
import store.shportfolio.user.domain.entity.User;

public class UserDeleteEvent extends DomainEvent<User> {

    public UserDeleteEvent(User entity) {
        super(entity);
    }
}
