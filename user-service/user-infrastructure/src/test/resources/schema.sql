CREATE TABLE USER_ENTITY (
                             userId VARCHAR(255) NOT NULL PRIMARY KEY,
                             username VARCHAR(255) NOT NULL,
                             password VARCHAR(255),
                             email VARCHAR(255) NOT NULL UNIQUE,
                             createdAt TIMESTAMP,
                             accountStatus VARCHAR(50)
);