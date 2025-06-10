package store.shportfolio.user.servicetest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.application.UserApplicationServiceImpl;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.UserDuplicatedException;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.event.UserDeleteEvent;
import store.shportfolio.user.domain.exception.DomainException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private UserApplicationServiceImpl userApplicationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtHandler jwtHandler;

    private PasswordEncoder passwordEncoder;

    private final String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userApplicationService = new UserApplicationServiceImpl(
                userRepository,
                passwordEncoder,
                new UserDomainServiceImpl(),
                new UserDataMapper(),
                jwtHandler);
    }

    @Test
    @DisplayName("create (user && duplicated user) test")
    public void createUser() {
        // given

        // normal

        String email = "test@test.com";
        String username = "test";
        String password = "testPwd";
        UserCreateCommand userCreateCommand = new UserCreateCommand(email, username, password);
        userCreateCommand.setToken("Token");
        String encodedPassword = passwordEncoder.encode(userCreateCommand.getPassword());
        User user = User.createUser(userId, email, username, encodedPassword,false);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(jwtHandler.getEmailFromToken(new Token("Token")))
                .thenReturn(email);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        // abnormal

        String duplicatedEmail = "duplicated@test.com";
        String duplicatedPassword = passwordEncoder.encode("duplicated");
        UserCreateCommand duplicatedUserCreateCommand =
                new UserCreateCommand(duplicatedEmail, "duplicated", duplicatedPassword);
        duplicatedUserCreateCommand.setToken("duplicatedToken");
        User duplicatedUser = User.createUser(UUID.randomUUID().toString(),
                duplicatedEmail, "duplicated", duplicatedPassword,false);

        Mockito.when(userRepository.findByEmail(duplicatedEmail))
                .thenReturn(Optional.of(duplicatedUser));
        Mockito.when(jwtHandler.getEmailFromToken(new Token("duplicatedToken")))
                .thenReturn(duplicatedEmail);

        // when
        UserCreateResponse createdUser = userApplicationService.createUser(userCreateCommand);

        // then
        UserDuplicatedException userDuplicatedException = Assertions.
                assertThrows(UserDuplicatedException.class,
                        () -> userApplicationService.createUser(duplicatedUserCreateCommand));

        Assertions.assertEquals("User with email duplicated@test.com already exists",
                userDuplicatedException.getMessage());
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(email, createdUser.getEmail());
        Assertions.assertEquals(username, createdUser.getUsername());

        Mockito.verify(userRepository, Mockito.times(2)).findByEmail(Mockito.any(String.class));
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("find one user")
    public void findOneUser() {
        // given
        String email = "test@test.com";
        String username = "test";
        String encryptedPassword = passwordEncoder.encode("password");
        User user = User.createUser(userId, email, username, encryptedPassword,false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // when
        UserTrackResponse userTrackResponse = userApplicationService.trackQueryUser(new UserTrackQuery(userId));

        // then
        Assertions.assertNotNull(userTrackResponse);
        Assertions.assertEquals(userId, userTrackResponse.getUserId());
        Assertions.assertEquals(username, userTrackResponse.getUsername());
    }

    @Test
    @DisplayName("user normal updating test")
    public void updateUserPassword() {
        // given
        String username = "test";
        String email = "test@test.com";
        String password = "testPwd";
        String encryptedPassword = passwordEncoder.encode(password);
        String currentPassword = "testPwd";
        String newPassword = "newPwd";
        String encryptedNewPassword = passwordEncoder.encode(newPassword);

        UserUpdateCommand userUpdateCommand = new UserUpdateCommand(userId, currentPassword, newPassword);

        User user = User.createUser(userId, email, username, encryptedPassword,false);
        User updatedUser = User.createUser(userId, email, username, encryptedNewPassword,false);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).
                thenReturn(updatedUser);

        // when
        userApplicationService.updateUser(userUpdateCommand);
        // then

        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("updating password but, not match current password")
    public void updateWrongCurrentPassword() {

        // given
        String username = "test";
        String email = "test@test.com";
        String password = "testPwd";
        String encryptedPassword = passwordEncoder.encode(password);

        String currentPassword = "wrongPassword";
        String newPassword = "newPwd";

        UserUpdateCommand userUpdateCommand = new UserUpdateCommand(userId, currentPassword, newPassword);

        User user = User.createUser(userId, email, username, encryptedPassword,false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        DomainException domainException = Assertions.
                assertThrows(DomainException.class, () -> userApplicationService.updateUser(userUpdateCommand));

        // then
        Assertions.assertEquals("Password does not match", domainException.getMessage());
    }

    @Test
    @DisplayName("delete user test")
    public void deleteUser() {

        // given
        String email = "test@test.com";
        String username = "test";
        String password = "testPwd";
        String encryptedPassword = passwordEncoder.encode(password);

        UserDeleteCommand userDeleteCommand = new UserDeleteCommand(userId);

        User user = User.createUser(userId, email, username, encryptedPassword,false);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserDeleteEvent userDeleteEvent = userApplicationService.deleteUser(userDeleteCommand);

        // then
        Mockito.verify(userRepository,Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository,Mockito.times(1)).remove(userId);

        Assertions.assertNotNull(userDeleteEvent);
        Assertions.assertEquals(userDeleteEvent.getEntity().getId().getValue(), userId);
    }
}
