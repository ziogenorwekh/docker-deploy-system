package store.shportfolio.user.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.UserEmailDuplicatedException;
import store.shportfolio.user.application.exception.UserNotFoundException;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;

import java.util.UUID;
@Slf4j
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
        log.info("tracking user -> {}",user.getEmail().getValue());
        return userDataMapper.toUserTrackResponse(user);
    }

    @Override
    public UserCreateResponse createUser(UserCreateCommand userCreateCommand) {
        String token = userCreateCommand.getToken();
        String authenticatedEmail = jwtHandler.getEmailFromToken(new Token(token));

        userRepository.findByEmail(authenticatedEmail).ifPresent(user -> {
            throw new UserEmailDuplicatedException(
                    String.format("User with email %s already exists", user.getEmail().getValue()));
        });

        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        UUID userId = UUID.randomUUID();

        User createdUser = userDomainService.createUser(userId, userCreateCommand.getEmail(),
                userCreateCommand.getUsername(), encryptedPassword);

        User savedUser = userRepository.save(createdUser);
        log.info("successful save user -> {}",savedUser.getEmail().getValue());
        return userDataMapper.toUserCreateResponse(savedUser);
    }

    @Override
    public void updateUser(UserUpdateCommand userUpdateCommand) {
        User user = userRepository.findById(userUpdateCommand.getUserId()).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %s not found", userUpdateCommand.getUserId())));

        String encryptedNewPassword = passwordEncoder.encode(userUpdateCommand.getNewPassword());

        User updatedUser = userDomainService.updateUser(user,
                userUpdateCommand.getCurrentPassword(), encryptedNewPassword);

        userRepository.save(updatedUser);
        log.info("successful update user -> {}",updatedUser.getEmail().getValue());
    }

    @Override
    public UserDeleteEvent deleteUser(UserDeleteCommand userDeleteCommand) {
        User user = userRepository.findById(userDeleteCommand.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException(String.format("User with id %s not found", userDeleteCommand.getUserId()));
        });
        UserDeleteEvent userDeleteEvent = userDomainService.deleteUser(user);
        userRepository.remove(user.getId().getValue());
        log.info("successful delete user -> {}",userDeleteCommand.getUserId());
        return userDeleteEvent;
    }
}
