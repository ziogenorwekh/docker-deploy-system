package store.shportfolio.user.application;

import jakarta.validation.Valid;
import store.shportfolio.user.application.command.*;

public interface UserApplicationService {

    UserTrackResponse trackQueryUser(@Valid UserTrackQuery userTrackQuery);

    UserCreateResponse createUser(@Valid UserCreateCommand userCreateCommand);

    void updateUser(@Valid UserUpdateCommand userUpdateCommand);

    void deleteUser(@Valid UserDeleteCommand userDeleteCommand);

}
