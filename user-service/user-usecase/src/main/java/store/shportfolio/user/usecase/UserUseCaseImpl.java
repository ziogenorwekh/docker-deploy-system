package store.shportfolio.user.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.exception.UserDuplicatedException;
import store.shportfolio.user.usecase.exception.UserNotFoundException;
import store.shportfolio.user.usecase.mapper.UserDataMapper;
import store.shportfolio.user.usecase.ports.input.UserUseCase;
import store.shportfolio.user.usecase.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.usecase.ports.output.security.JwtPort;
import store.shportfolio.user.usecase.ports.output.security.PasswordPort;

import java.util.UUID;
@Slf4j
@Service
@Validated
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordPort passwordEncoder;
    private final UserDomainService userDomainService;
    private final UserDataMapper userDataMapper;
    private final JwtPort jwtHandler;


    public UserUseCaseImpl(UserRepository userRepository, PasswordPort passwordEncoder,
                           UserDomainService userDomainService, UserDataMapper userDataMapper,
                           JwtPort jwtHandler) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDomainService = userDomainService;
        this.userDataMapper = userDataMapper;
        this.jwtHandler = jwtHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public UserTrackResponse trackQueryUser(UserTrackQuery userTrackQuery) {
        User user = userRepository.findById(userTrackQuery.getUserId()).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id %s not found", userTrackQuery.getUserId())));
        log.trace("tracking user -> {}",user.getEmail().getValue());
        log.debug("tracking user -> {}",user.getEmail().getValue());
        return userDataMapper.toUserTrackResponse(user);
    }

    @Override
    @Transactional
    public UserCreateResponse createUser(UserCreateCommand userCreateCommand) {
        String token = userCreateCommand.getToken();
        String authenticatedEmail = jwtHandler.getEmailFromToken(new Token(token));

        isValidUserNameAndEmail(authenticatedEmail, userCreateCommand.getUsername());

        String encryptedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        String userId = UUID.randomUUID().toString();

        User createdUser = userDomainService.createUser(userId, userCreateCommand.getEmail(),
                userCreateCommand.getUsername(), encryptedPassword);

        User savedUser = userRepository.save(createdUser);
        log.info("successful save user -> {}",savedUser.getEmail().getValue());
        return userDataMapper.toUserCreateResponse(savedUser);
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateCommand userUpdateCommand) {
        User user = userRepository.findById(userUpdateCommand.getUserId()).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %s not found", userUpdateCommand.getUserId())));

        String encryptedNewPassword = passwordEncoder.encode(userUpdateCommand.getNewPassword());

        User updatedUser = userDomainService.updateUser(user, encryptedNewPassword);

        userRepository.save(updatedUser);
        log.info("successful updating user -> {}",updatedUser.getEmail().getValue());
    }

    @Override
    @Transactional
    public UserDeleteEvent deleteUser(UserDeleteCommand userDeleteCommand) {
        User user = userRepository.findById(userDeleteCommand.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException(String.format("User with id %s not found", userDeleteCommand.getUserId()));
        });
        UserDeleteEvent userDeleteEvent = userDomainService.deleteUser(user);
        userRepository.remove(user.getId().getValue());
        log.info("successful delete user -> {}",userDeleteCommand.getUserId());
        return userDeleteEvent;
    }

    private void isValidUserNameAndEmail(String email, String username) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new UserDuplicatedException(String.format("User with email %s already exists", email));
        });
        userRepository.findByUsername(username).ifPresent(user -> {
            throw new UserDuplicatedException(String.format("User with username %s already exists", username));
        });
    }
}
