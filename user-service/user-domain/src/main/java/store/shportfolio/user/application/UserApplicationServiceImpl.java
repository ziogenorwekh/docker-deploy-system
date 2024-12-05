package store.shportfolio.user.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.UserNotFoundException;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;

@Service
@Validated
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDomainService userDomainService;
    private final UserDataMapper userDataMapper;
    private final JwtHandler jwtHandler;
    public UserApplicationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                      UserDomainService userDomainService, UserDataMapper userDataMapper,
                                      JwtHandler jwtHandler) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDomainService = userDomainService;
        this.userDataMapper = userDataMapper;
        this.jwtHandler = jwtHandler;
    }


    @Override
    public UserTrackResponse trackQueryUser(UserTrackQuery userTrackQuery) {
        User user = userRepository.findById(userTrackQuery.getUserId()).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id %s not found", userTrackQuery.getUserId())));
        return userDataMapper.toUserTrackResponse(user);
    }

    @Override
    public UserCreateResponse createUser(UserCreateCommand userCreateCommand) {

        return null;
    }

    @Override
    public void updateUser(UserUpdateCommand userUpdateCommand) {

    }

    @Override
    public void deleteUser(UserDeleteCommand userDeleteCommand) {

    }
}
