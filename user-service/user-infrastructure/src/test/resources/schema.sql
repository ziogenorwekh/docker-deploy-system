CREATE TABLE USER_ENTITY (
                             userId VARCHAR(255) NOT NULL PRIMARY KEY,
                             username VARCHAR(255) NOT NULL UNIQUE,
                             password VARCHAR(255) NOT NULL,
                             email VARCHAR(255) NOT NULL UNIQUE,
                             createdAt TIMESTAMP,
                             accountStatus VARCHAR(50)
);