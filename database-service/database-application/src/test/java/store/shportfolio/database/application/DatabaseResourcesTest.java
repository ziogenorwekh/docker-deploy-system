package store.shportfolio.database.application;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.api.DatabaseResources;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;

import java.util.UUID;

@ContextConfiguration(classes = DatabaseResourcesTestConfig.class)
@WebMvcTest(DatabaseResources.class)
public class DatabaseResourcesTest {

    @Autowired
    private MockMvc mockMvc;


    private ObjectMapper objectMapper;

    private DatabaseResources databaseResources;

    @Mock
    private UserServiceClient userServiceClient;

    @Autowired
    private DatabaseApplicationService databaseApplicationService;


    private final String token = "token";
    private final String userId = UUID.randomUUID().toString();
    private final String username = "testUsername";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        databaseResources = new DatabaseResources(userServiceClient, databaseApplicationService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(databaseResources)
                .build();
    }

    @Test
    @DisplayName("database create api test")
    public void databaseCreateApiTest() throws Exception {
        // given
        UserGlobal userGlobal = new UserGlobal(userId, username);
        DatabaseCreateCommand databaseCreateCommand = new DatabaseCreateCommand("databasePassword");
        DatabaseCreateResponse databaseCreateResponse = DatabaseCreateResponse.builder()
                .databasePassword("databasePassword")
                .build();
        Mockito.when(userServiceClient.getUserInfo(token)).thenReturn(userGlobal);
        Mockito.when(databaseApplicationService.createDatabase(databaseCreateCommand,userGlobal))
                .thenReturn(databaseCreateResponse);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/databases")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(databaseCreateCommand)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.databasePassword")
                        .value("databasePassword"));
    }
}
