package test.respository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.infrastructure.database.jpa.adapter.UserRepositoryImpl;

import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2,
        replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {


    @Autowired
    private UserRepositoryImpl userRepository;

    private final String userId = UUID.randomUUID().toString();
    private final String email = "test@test.com";
    private final String username = "test";
    private final String password = "$2a$10$wHvChXuLtBh/nn9tDLCdweLidg.A9KHEjRyzxURscADm9O8anp2sO";


    @Test
    @DisplayName("create user using jpa")
    public void createUserByDb() {

        // given
        User user = User.createUser(userId, email, username, password,false);

        // when
        User saved = userRepository.save(user);

        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(userId, saved.getId().getValue());
        Assertions.assertEquals(email, saved.getEmail().getValue());
        Assertions.assertEquals(username, saved.getUsername().getValue());
    }

    @Test
    @DisplayName("find user using jpa")
    public void findUserByDb() {

        // given
        User user = User.createUser(userId, email, username, password,false);
        userRepository.save(user);

        // when
        Optional<User> optionalUser = userRepository.findById(userId);

        // then
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(userId, optionalUser.get().getId().getValue());
        Assertions.assertEquals(email, optionalUser.get().getEmail().getValue());
        Assertions.assertEquals(username, optionalUser.get().getUsername().getValue());

        System.out.println("optionalUser.get().getEmail().getValue() = " + optionalUser.get().getEmail().getValue());
    }

}
