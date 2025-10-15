package store.shportfolio.database.application;

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
import store.shportfolio.database.application.openfeign.UserServiceClient;
import store.shportfolio.database.usecase.DatabaseUseCase;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

import java.util.UUID;

@ContextConfiguration(classes = DatabaseResourcesTestConfig.class)
@WebMvcTest({DatabaseResources.class})
public class DatabaseResourcesTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private DatabaseResources databaseResources;

    @Mock
    private UserServiceClient userServiceClient;

    @Autowired
    private DatabaseUseCase databaseUseCase;


    private final String token = "token";
    private final String userId = UUID.randomUUID().toString();
    private final String username = "testUsername";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        databaseResources = new DatabaseResources(databaseUseCase);
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
        Mockito.when(databaseUseCase.createDatabase(Mockito.any(DatabaseCreateCommand.class)
                , Mockito.any(UserGlobal.class))).thenReturn(databaseCreateResponse);
        // when, then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/databases")
                        .header("X-Authenticated-Username", userGlobal.getUsername())
                        .header("X-Authenticated-UserId",userGlobal.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(databaseCreateCommand)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.databasePassword")
                        .value("databasePassword"));
    }

    @Test
    @DisplayName("get database info api test")
    public void getDatabaseInfoApiTest() throws Exception {
        // given
        UserGlobal userGlobal = new UserGlobal(userId, username);
        DatabaseTrackResponse databaseTrackResponse = DatabaseTrackResponse.builder()
                .databaseName("databaseName")
                .databasePassword("databasePassword")
                .databaseUsername("databaseUsername")
                .accessUrl("accessUrl")
                .build();
        Mockito.when(databaseUseCase.trackDatabase(Mockito.any(DatabaseTrackQuery.class)))
                .thenReturn(databaseTrackResponse);

        // when, then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/databases")
                        .header("X-Authenticated-Username", userGlobal.getUsername())
                        .header("X-Authenticated-UserId",userGlobal.getUserId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.databasePassword")
                        .value("databasePassword"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.databaseUsername")
                        .value("databaseUsername"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessUrl")
                        .value("accessUrl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.databaseName")
                        .value("databaseName"));
    }


}
