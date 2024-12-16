package store.shportfolio.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.api.UserResources;
import store.shportfolio.user.application.command.*;
import store.shportfolio.user.application.openfeign.DatabaseServiceClient;
import store.shportfolio.user.application.openfeign.DeployServiceClient;

import java.time.LocalDateTime;

@ContextConfiguration(classes = UserResourcesTestConfig.class)
@WebMvcTest(UserResources.class)
public class UserResourcesTest {


    @Autowired
    private MockMvc mockMvc;


    private ObjectMapper objectMapper;

    private final String email = "test@email.com";
    private final String password = "testPassword";
    private final String username = "testUsername";
    private final String token = "test-token";
    private final String userId = "test-user-id";

    @Autowired
    private UserApplicationService userApplicationService;
    private UserResources userResources;

    @Mock
    private DatabaseServiceClient databaseServiceClient;

    @Mock
    private DeployServiceClient deployServiceClient;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        userResources = new UserResources(userApplicationService,databaseServiceClient,deployServiceClient);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userResources)
                .build();
    }


    @Test
    @DisplayName("user create api test")
    public void userCreateApiTest() throws Exception {
        UserCreateCommand userCreateCommand = new UserCreateCommand(email, password, username);

        UserCreateResponse userCreateResponse = new UserCreateResponse(userId, username, email);

        Mockito.when(userApplicationService.createUser(Mockito.any(UserCreateCommand.class)))
                .thenReturn(userCreateResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateCommand)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username));
    }

    @Test
    @DisplayName("user retrieve test")
    public void userRetrieveApiTest() throws Exception {

        // given
        UserTrackResponse userTrackResponse = new UserTrackResponse(userId, username, email, LocalDateTime.now());

        Mockito.when(userApplicationService.trackQueryUser(Mockito.any(UserTrackQuery.class)))
                .thenReturn(userTrackResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username));
    }

}
