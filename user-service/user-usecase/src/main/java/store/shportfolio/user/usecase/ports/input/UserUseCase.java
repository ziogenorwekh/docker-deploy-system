package store.shportfolio.user.usecase.ports.input;

import jakarta.validation.Valid;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.domain.event.UserDeleteEvent;

public interface UserUseCase {

    UserTrackResponse trackQueryUser(@Valid UserTrackQuery userTrackQuery);

    UserCreateResponse createUser(@Valid UserCreateCommand userCreateCommand);

    void updateUser(@Valid UserUpdateCommand userUpdateCommand);

    UserDeleteEvent deleteUser(@Valid UserDeleteCommand userDeleteCommand);

}
