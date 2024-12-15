package store.shportfolio.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.UserAuthenticationService;
import store.shportfolio.user.application.api.UserSecurityResources;
import store.shportfolio.user.application.command.EmailSendCommand;
import store.shportfolio.user.application.command.EmailVerificationCommand;
import store.shportfolio.user.application.command.LoginCommand;
import store.shportfolio.user.application.command.LoginResponse;

import java.util.UUID;

@ContextConfiguration(classes = UserResourcesTestConfig.class)
@WebMvcTest(UserSecurityResources.class)
public class UserSecurityResourcesTest {

    private UserSecurityResources userSecurityResources;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    private ObjectMapper objectMapper;

    private final String email = "test@email.com";
    private final String password = "testPassword";
    private final String userId = UUID.randomUUID().toString();
    private final String token = "testToken";


    @BeforeEach
    void setUp() {
        userSecurityResources = new UserSecurityResources(userAuthenticationService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userSecurityResources)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("user login test")
    public void userLoginTest() throws Exception {

        // given
        LoginCommand loginCommand = new LoginCommand(email, password);
        LoginResponse loginResponse = new LoginResponse(userId, email, token);

        Mockito.when(userAuthenticationService.login(Mockito.any(LoginCommand.class)))
                .thenReturn(loginResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCommand)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("send email api test")
    public void sendEmailApiTest() throws Exception {
        EmailSendCommand emailSendCommand = new EmailSendCommand(email);

        Mockito.doNothing().when(userAuthenticationService)
                .sendEmail(emailSendCommand);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/user/mail-send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailSendCommand)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("verify mail auth code api test")
    public void verifyMailAuthCodeApiTest() throws Exception {
        String code = "123456";
        Token tokenVO = new Token(token);
        EmailVerificationCommand emailVerificationCommand = new EmailVerificationCommand(email, code);

        Mockito.when(userAuthenticationService.verifyEmail(Mockito.any(EmailVerificationCommand.class)))
                .thenReturn(tokenVO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/user/verify-mail")
                .content(objectMapper.writeValueAsString(emailVerificationCommand))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token")
                        .value(token));
    }

}
