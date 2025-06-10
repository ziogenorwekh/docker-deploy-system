package store.shportfolio.database.infrastructure.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.infrastructure.exception.DatabaseSchemaException;

@Slf4j
@Repository
public class DatabaseSchemaManagement {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseSchemaManagement(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createSchema(String databaseName, String databaseUsername, String databasePassword) {
        integrationTryCatch(() -> {
            jdbcTemplate.execute("CREATE DATABASE " + databaseName + ";");
            jdbcTemplate.execute("CREATE USER " + "'" + databaseUsername + "'"
                    + "@" + "'" + "%" + "'" + "IDENTIFIED BY" + "'" + databasePassword + "';");
            jdbcTemplate.execute("GRANT ALL ON " +
                    databaseName + ".* TO " + "'" + databaseUsername + "'" + "@'%'");

            log.info("Database and user created successfully: {}", databaseName);
        }, "create schema error");
    }

    public void dropSchema(String databaseName, String databaseUsername) {
        integrationTryCatch(() -> {
            jdbcTemplate.execute("DROP DATABASE " + databaseName);
            jdbcTemplate.execute("DROP USER " + "'" + databaseUsername + "'" + "@'%'");
        }, "drop schema error");
    }

    private void integrationTryCatch(Runnable action, String errorMessage) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("{}: {}", e.getClass().getSimpleName(), errorMessage);
            log.error("error is {}", e.getMessage());
            throw new DatabaseSchemaException(errorMessage, e);
        }
    }

}
