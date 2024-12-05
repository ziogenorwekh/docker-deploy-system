package store.shportfolio.user.servicetest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import store.shportfolio.user.application.JwtHandler;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.UserApplicationServiceImpl;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.valueobject.Password;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private UserApplicationServiceImpl userApplicationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtHandler jwtHandler;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userApplicationService = new UserApplicationServiceImpl(
                userRepository,
                passwordEncoder,
                new UserDomainServiceImpl(),
                new UserDataMapper(),
                jwtHandler);
    }

    @Test
    @DisplayName("create user test")
    public void createUser() {
        // given
        String email = "test@test.com";
        String username = "test";
        String password = "testPwd";
        UserCreateCommand userCreateCommand = new UserCreateCommand(email, username, password);
        UserCreateResponse userCreateResponse = new UserCreateResponse(userId, username, email);
        User user = User.createUser(userId, email, username, password);
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user); // userRepository Mock 설정

        // when
        UserCreateResponse createdUser = userApplicationService.createUser(userCreateCommand);

        // then
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(email, createdUser.getEmail());
        Assertions.assertEquals(username, createdUser.getUsername());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class)); // userRepository의 save 메서드 호출 확인
    }

    @Test
    @DisplayName("find one user")
    public void findOneUser() {
        // given
        String email = "test@test.com";
        String username = "test";
        User user = User.createUser(userId, email, username, "testPwd");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        Mockito.when(userDataMapper.toUserTrackResponse(Mockito.any(User.class)))
//                .thenReturn(new UserTrackResponse(userId, username, email, user.getCreatedAt()));
        // when
        UserTrackResponse userTrackResponse = userApplicationService.trackQueryUser(new UserTrackQuery(userId));

        // then
        Assertions.assertNotNull(userTrackResponse);
        Assertions.assertEquals(userId, userTrackResponse.getUserId());
        Assertions.assertEquals(username, userTrackResponse.getUsername());
    }

    @Test
    @DisplayName("user update test")
    public void updateUser() {
        // given
        String username = "test";
        String email = "test@test.com";
        String password = "testPwd";

        String currentPassword = "testPwd";
        String newPassword = "newPwd";
        String wrongPassword = "wrongPwd";
        UserUpdateCommand userUpdateCommand = new UserUpdateCommand(userId, currentPassword, newPassword);
        UserUpdateCommand worngUserUpdateCommand = new UserUpdateCommand(userId, wrongPassword, newPassword);
        User user = User.createUser(userId, email, username, password);

        // when

        userApplicationService.updateUser(userUpdateCommand);
        userApplicationService.updateUser(worngUserUpdateCommand);
        // then

    }
}
