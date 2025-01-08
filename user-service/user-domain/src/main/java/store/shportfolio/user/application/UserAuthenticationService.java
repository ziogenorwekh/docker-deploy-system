package store.shportfolio.user.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.exception.LoginException;
import store.shportfolio.user.application.exception.UserNotFoundException;
import store.shportfolio.user.application.jwt.JwtHandler;
import store.shportfolio.user.application.mapper.UserDataMapper;
import store.shportfolio.user.application.ports.output.mail.MailSender;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.application.security.UserDetailsImpl;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;

@Slf4j
@Component
@Validated
public class UserAuthenticationService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;
    private final JwtHandler jwtHandler;
    private final MailSender mailSender;
    private final UserDataMapper userDataMapper;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository, UserDomainService userDomainService, JwtHandler jwtHandler,
                                     AuthenticationManager authenticationManager,
                                     UserDataMapper userDataMapper, MailSender mailSender) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.jwtHandler = jwtHandler;
        this.authenticationManager = authenticationManager;
        this.userDataMapper = userDataMapper;
        this.mailSender = mailSender;
    }


    @Transactional(readOnly = true)
    public LoginResponse login(LoginCommand loginCommand) {
        UsernamePasswordAuthenticationToken token;

        UserDetailsImpl userDetails;
        try {
            token = new UsernamePasswordAuthenticationToken(loginCommand.getEmail(),
                    loginCommand.getPassword());
            Authentication authenticate = authenticationManager.authenticate(token);
            userDetails = (UserDetailsImpl) authenticate.getPrincipal();
            log.info("login access user -> {}", userDetails.getEmail());
            Token jwtToken = jwtHandler.createLoginToken(userDetails.getEmail(), userDetails.getId());
            return userDataMapper.toLoginResponse(userDetails, jwtToken.getValue());
        } catch (BadCredentialsException e) {
            log.error("BadCredentialsException -> {}", e.getMessage());
            throw new BadCredentialsException("Login failed", e);
        } catch (DisabledException e) {
            log.error("DisabledException -> {}", e.getMessage());
            throw new DisabledException("The account is disabled", e);
        } catch (Exception e) {
            log.error("Exception -> {}", e.getMessage());
            throw new LoginException("An unexpected error occurred", e);
        }
    }

    @Transactional
    public Token loginByGoogle(String email, String userId, String username) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User googleUser = userDomainService.createGoogleUser(userId, email, username);
            userRepository.save(googleUser);
        }
        return jwtHandler.createLoginToken(email, userId);
    }

    @Transactional(readOnly = true)
    public User getUserByToken(Token token) {
        String userId = jwtHandler.getUserIdByToken(token);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with token: " + token));
    }

    public void sendEmail(EmailSendCommand emailSendCommand) {
        log.info("send email -> {}", emailSendCommand.getEmail());
        mailSender.sendMail(emailSendCommand.getEmail());
    }

    public Token verifyEmail(EmailVerificationCommand emailVerificationCommand) {
        Boolean isVerify = mailSender.verifyMail(emailVerificationCommand.getEmail(),
                emailVerificationCommand.getCode());

        if (!isVerify) {
            log.error("Email verification failed");
            throw new BadCredentialsException("Email verification failed");
        }
        log.info("successful verified email -> {}", emailVerificationCommand.getEmail());
        return jwtHandler
                .createSignupTemporaryToken(emailVerificationCommand.getEmail());
    }
}
