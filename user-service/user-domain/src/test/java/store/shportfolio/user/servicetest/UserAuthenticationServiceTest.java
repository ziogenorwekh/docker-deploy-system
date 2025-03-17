package store.shportfolio.user.servicetest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import store.shportfolio.common.domain.valueobject.Email;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.common.domain.valueobject.Username;
import store.shportfolio.user.application.UserAuthenticationService;
import store.shportfolio.user.application.command.EmailVerificationCommand;
import store.shportfolio.user.application.command.LoginCommand;
import store.shportfolio.user.application.command.LoginResponse;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.mail.MailSender;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.application.security.UserDetailsImpl;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.valueobject.AccountStatus;
import store.shportfolio.user.domain.valueobject.Password;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAuthenticationServiceTest {

    private UserAuthenticationService userAuthenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailSender mailSender;

    private UserDomainService userDomainService;

    private TestEnvironment env;

    private UserDataMapper userDataMapper;

    private JwtHandler jwtHandler;

    private final String email = "test@email.com";


    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        env = new TestEnvironment();
        userDataMapper = new UserDataMapper();
        jwtHandler = new JwtHandler(env);
        userDomainService = new UserDomainServiceImpl();
        userAuthenticationService = new UserAuthenticationService(
                userRepository, userDomainService,
                jwtHandler, authenticationManager, userDataMapper, mailSender
        );
    }

    @Test
    @DisplayName("verify email test")
    public void verifyEmailTest() {
        // given
        String code = "123456";

        String wrongCode = "1234567";
        EmailVerificationCommand command = new EmailVerificationCommand(email, code);
        EmailVerificationCommand wrongCommand = new EmailVerificationCommand(email, wrongCode);
        Mockito.when(mailSender.verifyMail(email, code)).thenReturn(true);
        Mockito.when(mailSender.verifyMail(email, wrongCode)).thenReturn(false);

        // when
        Token token = userAuthenticationService.verifyEmail(command);


        BadCredentialsException badCredentialsException = Assertions.assertThrows(BadCredentialsException.class, () -> {
            userAuthenticationService.verifyEmail(wrongCommand);
        });

        // then
        Assertions.assertTrue(isJwtToken(token));
        Assertions.assertTrue(isJwtTokenValid(token, "test-secret"));
        Assertions.assertNotNull(badCredentialsException);
        Assertions.assertEquals(badCredentialsException.getMessage(),
                "Email verification failed");
    }

    @Test
    @DisplayName("login method test")
    public void loginTest() {

        // given
        String userId = UUID.randomUUID().toString();
        String username = "test";
        String password = "123456";
        User registeredUser = new User(new UserId(userId), new Email(email), new Username(username),
                new Password(password), AccountStatus.ENABLED, LocalDateTime.now());


        Mockito.lenient().when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(registeredUser));
        UserDetailsImpl userDetails = new UserDetailsImpl(registeredUser);

        Authentication authentication = Mockito.mock(Authentication.class);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);

        Mockito.when(authenticationManager.authenticate(authenticationToken))
                .thenReturn(authentication);

        LoginCommand command = new LoginCommand(email, password);
        // when
        LoginResponse loginResponse = userAuthenticationService.login(command);
        // then

        Assertions.assertNotNull(loginResponse);
        Assertions.assertNotNull(loginResponse.getToken());
        Assertions.assertEquals(userId.toString(), loginResponse.getUserId());
        Assertions.assertEquals(email, loginResponse.getEmail());
        Assertions.assertTrue(isJwtToken(new Token(loginResponse.getToken())));
        Assertions.assertTrue(isJwtTokenValid(new Token(loginResponse.getToken()), "test-secret"));
    }

    //    @Test
    @DisplayName("get user by token")
    public void getUserByTokenTest() {

        String userId = UUID.randomUUID().toString();
        String username = "test";
        String password = "123456";

        User registeredUser = new User(new UserId(userId), new Email(email), new Username(username),
                new Password(password), AccountStatus.ENABLED, LocalDateTime.now());

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(registeredUser));

        Token loginToken = jwtHandler.createLoginToken(email, username, userId);
        // when
        User user = userAuthenticationService.getUserByToken(loginToken);
        // then
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(registeredUser.getId(), user.getId());
        Assertions.assertEquals(registeredUser.getUsername(), user.getUsername());
        Assertions.assertEquals(registeredUser.getPassword(), user.getPassword());
    }


    private boolean isJwtToken(Token token) {
        String value = token.getValue();
        String[] parts = value.split("\\.");
        return parts.length == 3;
    }

    private boolean isJwtTokenValid(Token token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // HMAC256 알고리즘 사용
            JWTVerifier verifier = JWT.require(algorithm).build(); // 검증 객체 생성
            DecodedJWT decodedJWT = verifier.verify(token.getValue()); // 토큰 검증 수행
            return true; // 유효한 토큰
        } catch (JWTVerificationException e) {
            return false; // 서명 검증 실패 또는 토큰 오류
        }
    }
}
