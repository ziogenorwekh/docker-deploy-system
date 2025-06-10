package store.shportfolio.user.application;

import jakarta.validation.Valid;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.domain.event.UserDeleteEvent;

public interface UserApplicationService {

    UserTrackResponse trackQueryUser(@Valid UserTrackQuery userTrackQuery);

    UserCreateResponse createUser(@Valid UserCreateCommand userCreateCommand);

    void updateUser(@Valid UserUpdateCommand userUpdateCommand);

    UserDeleteEvent deleteUser(@Valid UserDeleteCommand userDeleteCommand);

}
