-- WebAppEntity 테이블 생성
CREATE TABLE WEBAPP_ENTITY (
                               APPLICATION_ID VARCHAR(255) NOT NULL PRIMARY KEY,
                               USER_ID VARCHAR(255) NOT NULL,
                               APPLICATION_NAME VARCHAR(255) NOT NULL,
                               SERVER_PORT INT NOT NULL,
                               JAVA_VERSION INT NOT NULL,
                               APPLICATION_STATUS VARCHAR(255),
                               ERROR_MESSAGE TEXT,
                               CREATED_AT TIMESTAMP
);

-- StorageEntity 테이블 생성
CREATE TABLE STORAGE_ENTITY (
                                ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                APPLICATION_ID VARCHAR(255),
                                STORAGE_URL VARCHAR(255),
                                STORAGE_NAME VARCHAR(255)
);

-- DockerContainerEntity 테이블 생성
CREATE TABLE DOCKERCONTAINER_ENTITY (
                                        ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        APPLICATION_ID VARCHAR(255) NOT NULL,
                                        DOCKERCONTAINER_ID VARCHAR(255),
                                        ENDPOINT_URL VARCHAR(255) NOT NULL,
                                        DOCKERCONTAINER_STATUS VARCHAR(255)
);