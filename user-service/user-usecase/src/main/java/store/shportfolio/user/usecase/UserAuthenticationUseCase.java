package store.shportfolio.user.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.usecase.command.*;
import store.shportfolio.user.usecase.dto.UserAuthenticationDataDto;
import store.shportfolio.user.usecase.exception.CustomBadCredentialsException;
import store.shportfolio.user.usecase.exception.UserDuplicatedException;
import store.shportfolio.user.usecase.exception.UserNotFoundException;
import store.shportfolio.user.usecase.mapper.UserDataMapper;
import store.shportfolio.user.usecase.ports.output.mail.MailSender;
import store.shportfolio.user.usecase.ports.output.repository.UserRepository;
import store.shportfolio.user.usecase.ports.output.security.JwtPort;
import store.shportfolio.user.usecase.ports.output.security.UserAuthenticatePort;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;

@Slf4j
@Component
@Validated
public class UserAuthenticationUseCase implements store.shportfolio.user.usecase.ports.input.UserAuthenticationUseCase {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final JwtPort jwtPort;
    private final MailSender mailSender;
    private final UserDataMapper userDataMapper;
    private final UserAuthenticatePort userAuthenticatePort;

    @Autowired
    public UserAuthenticationUseCase(UserRepository userRepository, UserDomainService userDomainService,
                                     JwtPort jwtHandler,
                                     UserDataMapper userDataMapper, MailSender mailSender,
                                     UserAuthenticatePort userAuthenticatePort) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.jwtPort = jwtHandler;
        this.userDataMapper = userDataMapper;
        this.mailSender = mailSender;
        this.userAuthenticatePort = userAuthenticatePort;
    }


    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginCommand loginCommand) {
        UserAuthenticationDataDto authenticate = userAuthenticatePort
                .authenticate(loginCommand.getEmail(), loginCommand.getPassword());
        return userDataMapper.toLoginResponse(authenticate);
    }

    @Override
    @Transactional
    public Token loginByGoogle(String email, String userId, String username) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User googleUser = userDomainService.createGoogleUser(userId, email, username);
            userRepository.save(googleUser);
        }
        return jwtPort.createLoginToken(email, username, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByToken(Token token) {
        String userId = jwtPort.getUserIdByToken(token);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with token: " + token));
    }

    @Override
    public void sendEmail(EmailSendCommand emailSendCommand) {
        userRepository.findByEmail(emailSendCommand.getEmail()).ifPresent(user -> {
            throw new UserDuplicatedException("Email already exists");
        });
        log.info("send email -> {}", emailSendCommand.getEmail());
        mailSender.sendMail(emailSendCommand.getEmail());
    }

    @Override
    public Token verifyEmail(EmailVerificationCommand emailVerificationCommand) {
        Boolean isVerify = mailSender.verifyMail(emailVerificationCommand.getEmail(),
                emailVerificationCommand.getCode());

        if (!isVerify) {
            log.error("Email verification failed");
            throw new CustomBadCredentialsException("Email verification failed");
        }
        log.info("successful verified email -> {}", emailVerificationCommand.getEmail());
        return jwtPort
                .createSignupTemporaryToken(emailVerificationCommand.getEmail());
    }
}
