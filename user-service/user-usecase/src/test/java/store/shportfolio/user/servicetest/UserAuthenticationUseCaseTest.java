package store.shportfolio.user.servicetest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.usecase.*;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.dto.UserAuthenticationDataDto;
import store.shportfolio.user.usecase.exception.*;
import store.shportfolio.user.usecase.mapper.UserDataMapper;
import store.shportfolio.user.usecase.ports.output.mail.MailSender;
import store.shportfolio.user.usecase.ports.output.repository.UserRepository;
import store.shportfolio.user.usecase.ports.output.security.JwtPort;
import store.shportfolio.user.usecase.ports.output.security.UserAuthenticatePort;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserAuthenticationUseCaseTest {

    @InjectMocks
    private UserAuthenticationUseCase userAuthenticationUseCase;

    @Mock
    private UserRepository userRepository;

    private UserDomainService userDomainService;

    @Mock
    private JwtPort jwtPort;

    @Mock
    private MailSender mailSender;

    private UserDataMapper userDataMapper;

    @Mock
    private UserAuthenticatePort userAuthenticatePort;

    private final String email = "email@test.com";
    private final String password = "password";
    private final String userId = UUID.randomUUID().toString();
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        userDomainService = new UserDomainServiceImpl();
        userDataMapper = new UserDataMapper();
        userAuthenticationUseCase = new UserAuthenticationUseCase(userRepository, userDomainService, jwtPort
                , userDataMapper, mailSender, userAuthenticatePort);
    }


    @Test
    void login_shouldReturnLoginResponse() {
        LoginCommand cmd = new LoginCommand(email, password);
        UserAuthenticationDataDto dto = mock(UserAuthenticationDataDto.class);

        when(userAuthenticatePort.authenticate(email, password)).thenReturn(dto);

        LoginResponse result = userAuthenticationUseCase.login(cmd);

        assertNotNull(result);
        verify(userAuthenticatePort).authenticate(email, password);
    }

    @Test
    void loginByGoogle_whenUserDoesNotExist_shouldCreateAndSaveUserAndReturnToken() {
        Token token = mock(Token.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtPort.createLoginToken(email, username, userId)).thenReturn(token);

        Token result = userAuthenticationUseCase.loginByGoogle(email, userId, username);

        assertEquals(token, result);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserByToken_whenUserExists_shouldReturnUser() {
        Token token = mock(Token.class);
        User user = mock(User.class);

        when(jwtPort.getUserIdByToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userAuthenticationUseCase.getUserByToken(token);

        assertEquals(user, result);
        verify(jwtPort).getUserIdByToken(token);
    }

    @Test
    void getUserByToken_whenUserDoesNotExist_shouldThrowException() {
        Token token = mock(Token.class);

        when(jwtPort.getUserIdByToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userAuthenticationUseCase.getUserByToken(token));
    }

    @Test
    void sendEmail_whenEmailExists_shouldThrowException() {
        EmailSendCommand cmd = new EmailSendCommand(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));

        assertThrows(UserDuplicatedException.class, () -> userAuthenticationUseCase.sendEmail(cmd));
    }

    @Test
    void sendEmail_whenEmailDoesNotExist_shouldSendMail() {
        EmailSendCommand cmd = new EmailSendCommand(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        userAuthenticationUseCase.sendEmail(cmd);

        verify(mailSender).sendMail(email);
    }

    @Test
    void verifyEmail_whenVerificationFails_shouldThrowException() {
        EmailVerificationCommand cmd = new EmailVerificationCommand(email, "code");
        when(mailSender.verifyMail(email, "code")).thenReturn(false);

        assertThrows(CustomBadCredentialsException.class, () -> userAuthenticationUseCase.verifyEmail(cmd));
    }

    @Test
    void verifyEmail_whenVerificationSucceeds_shouldReturnToken() {
        EmailVerificationCommand cmd = new EmailVerificationCommand(email, "code");
        Token token = mock(Token.class);

        when(mailSender.verifyMail(email, "code")).thenReturn(true);
        when(jwtPort.createSignupTemporaryToken(email)).thenReturn(token);

        Token result = userAuthenticationUseCase.verifyEmail(cmd);

        assertEquals(token, result);
    }
}
