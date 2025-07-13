package store.shportfolio.user.servicetest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.usecase.UserUseCaseImpl;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.exception.UserNotFoundException;
import store.shportfolio.user.usecase.mapper.UserDataMapper;
import store.shportfolio.user.usecase.ports.output.repository.UserRepository;
import store.shportfolio.user.usecase.ports.output.security.JwtPort;
import store.shportfolio.user.usecase.ports.output.security.PasswordPort;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserServiceTest {

    private UserUseCaseImpl userUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordPort passwordPort;

    @Mock
    private UserDomainService userDomainService;

    @Mock
    private UserDataMapper userDataMapper;

    @Mock
    private JwtPort jwtPort;

    private User dummyUser;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userDomainService = new UserDomainServiceImpl();
        userUseCase = new UserUseCaseImpl(userRepository, passwordPort, userDomainService, userDataMapper, jwtPort);
        dummyUser = User.createUser(
                userId.toString(),
                "email@test.com",
                "username",
                "$2a$10$7QsP9L2DfjMkQq3x5RlnOuAcRQbD80uvS7TrAnV7A6WDDGpKNzNay",
                false
        );
    }

    @Test
    void createUserTest() {
        UserCreateCommand cmd = new UserCreateCommand("email@test.com", "username", "password");

        when(jwtPort.getEmailFromToken(any())).thenReturn("email@test.com");
        when(passwordPort.encode("password")).thenReturn("hashedPw");
        when(userRepository.save(any(User.class))).thenReturn(dummyUser);
        UserCreateResponse dummyResponse = mock(UserCreateResponse.class);
        when(userDataMapper.toUserCreateResponse(dummyUser)).thenReturn(dummyResponse);

        UserCreateResponse response = userUseCase.createUser(cmd);

        Assertions.assertNotNull(response);
        Mockito.verify(jwtPort).getEmailFromToken(any());
        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    void trackQueryUserExceptForNotExistUser() {
        String userId = UUID.randomUUID().toString();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userUseCase.trackQueryUser(new UserTrackQuery(userId))
        );

        Mockito.verify(userRepository).findById(userId);
    }

    @Test
    void updateUserNormalUpdate() {
        // given
        String userId = dummyUser.getId().getValue();
        User updatedUser = User.createUser(
                userId.toString(),
                "email@test.com",
                "username",
                "$2a$10$7QsP9L2DfjMkQq3x5RlnOuAcRQbD80uvS7TrAnV7A6WDDGpKNzNay",
                false
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));
        when(passwordPort.encode(Mockito.anyString())).thenReturn("$2a$10$7QsP9L2DfjMkQq3x5RlnOuAcRQbD80uvS7TrAnV7A6WDDGpKNzNay");
        when(userRepository.save(any())).thenReturn(updatedUser);
        // when
        userUseCase.updateUser(new UserUpdateCommand(userId, "newPw"));

        // then
        Mockito.verify(userRepository,Mockito.times(1)).save(updatedUser);
    }

    @Test
    void deleteUserNormalDelete() {
        // given
        String userId = dummyUser.getId().getValue();

        when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(dummyUser));
        // when
        UserDeleteEvent result = userUseCase.deleteUser(new UserDeleteCommand(userId));

        // then
        Assertions.assertNotNull(result);
        Mockito.verify(userRepository, Mockito.times(1)).remove(userId);
    }
}
