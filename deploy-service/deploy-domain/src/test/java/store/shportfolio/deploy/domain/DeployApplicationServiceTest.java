package store.shportfolio.deploy.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.WebHandler;
import store.shportfolio.common.domain.valueobject.*;
import store.shportfolio.deploy.application.DeployApplicationService;
import store.shportfolio.deploy.application.DeployApplicationServiceImpl;
import store.shportfolio.deploy.application.command.*;
import store.shportfolio.deploy.application.handler.DockerContainerHandler;
import store.shportfolio.deploy.application.handler.StorageHandler;
import store.shportfolio.deploy.application.handler.WebAppHandler;
import store.shportfolio.deploy.application.mapper.DeployDataMapper;
import store.shportfolio.deploy.application.ports.output.docker.DockerConnector;
import store.shportfolio.deploy.application.ports.output.repository.DockerContainerRepository;
import store.shportfolio.deploy.application.ports.output.repository.StorageRepository;
import store.shportfolio.deploy.application.ports.output.repository.WebAppRepository;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;
import store.shportfolio.deploy.application.vo.DockerCreated;
import store.shportfolio.deploy.application.vo.ResourceUsage;
import store.shportfolio.deploy.application.vo.StorageInfo;
import store.shportfolio.deploy.domain.entity.DockerContainer;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;
import store.shportfolio.deploy.domain.valueobject.DockerContainerId;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class DeployApplicationServiceTest {

    private DeployApplicationService deployApplicationService;

    private DockerContainerHandler dockerContainerHandler;

    private StorageHandler storageHandler;

    private WebAppHandler webAppHandler;

    private DeployDataMapper deployDataMapper;

    private DeployDomainService deployDomainService;

    @Mock
    private DockerConnector dockerConnector;

    @Mock
    private DockerContainerRepository containerRepository;

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private WebAppRepository webAppRepository;

    private List<String> createdFiles = new ArrayList<>();

    @Mock
    private S3Bucket s3Bucket;

    private final UUID userId = UUID.randomUUID();
    private final String username = "username";

    @BeforeEach
    public void setUp() {
        deployDomainService = new DeployDomainServiceImpl();
        dockerContainerHandler = new DockerContainerHandler(dockerConnector, containerRepository, deployDomainService);
        webAppHandler = new WebAppHandler(webAppRepository, deployDomainService);
        deployDataMapper = new DeployDataMapper();
        storageHandler = new StorageHandler(storageRepository, s3Bucket, deployDomainService);
        deployApplicationService = new DeployApplicationServiceImpl(
                dockerContainerHandler, storageHandler, webAppHandler, deployDataMapper
        );
    }

    @AfterEach
    public void cleanUp() throws IOException {
        // 임시 파일들을 정리하는 로직
        for (String filePath : createdFiles) {
            File file = new File(filePath);
            if (file.exists()) {
                Files.delete(Paths.get(filePath));
            }
        }
    }


    @Test
    @DisplayName("create WebApp test")
    public void testCreateWebApp() {
        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        DockerContainer dockerContainer = DockerContainer.builder().applicationId(applicationId).build();
        Storage storage = Storage.builder()
                .applicationId(applicationId).build();

        WebAppCreateCommand webAppCreateCommand = WebAppCreateCommand.builder()
                .applicationName("testApplication")
                .port(12000)
                .version(17)
                .build();
        UserGlobal userGlobal = UserGlobal.builder()
                .userId(userId.toString()).username(username).build();
        WebApp webApp = WebApp.builder()
                .userId(new UserId(userGlobal.getUserId()))
                .applicationId(applicationId)
                .applicationName(new ApplicationName(webAppCreateCommand.getApplicationName()))
                .javaVersion(new JavaVersion(webAppCreateCommand.getVersion()))
                .serverPort(new ServerPort(webAppCreateCommand.getPort()))
                .applicationStatus(ApplicationStatus.CREATED)
                .build();


        Mockito.when(storageRepository.save(Mockito.any(Storage.class))).thenReturn(storage);
        Mockito.when(containerRepository.save(Mockito.any(DockerContainer.class)))
                .thenReturn(dockerContainer);
        Mockito.when(webAppRepository.save(Mockito.any(WebApp.class)))
                .thenReturn(webApp);
        // when

        WebAppCreateResponse webAppCreateResponse = deployApplicationService
                .createWebApp(userGlobal, webAppCreateCommand);
        // then

        Assertions.assertNotNull(webAppCreateResponse);
        Assertions.assertEquals(applicationId.getValue(), webAppCreateResponse.getApplicationId());
        Assertions.assertEquals(webAppCreateCommand.getApplicationName(), webAppCreateResponse.getApplicationName());
        Mockito.verify(storageRepository, Mockito.times(1)).save(Mockito.any(Storage.class));
        Mockito.verify(containerRepository, Mockito.times(1)).save(Mockito.any(DockerContainer.class));
        Mockito.verify(webAppRepository, Mockito.times(1)).save(Mockito.any(WebApp.class));
    }

    @Test
    @DisplayName("start container test")
    public void testStartContainer() {

        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        DockerContainer dockerContainer = DockerContainer.builder()
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .dockerContainerStatus(DockerContainerStatus.STOPPED)
                .endPointUrl("http://localhost:8080")
                .applicationId(applicationId)
                .build();

        Mockito.when(containerRepository.findByApplicationId(Mockito.eq(applicationId.getValue())))
                .thenReturn(Optional.of(dockerContainer));
        Mockito.when(dockerConnector.startContainer(Mockito.any())).thenReturn(true);
        Mockito.when(containerRepository.save(Mockito.any(DockerContainer.class)))
                .thenReturn(dockerContainer);
        // when, then

        deployApplicationService.startContainer(new WebAppTrackQuery(applicationId.getValue()),
                new UserGlobal(userId.toString(), username));
    }

    @Test
    @DisplayName("stop container test")
    public void testStopContainer() {
        // given

        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        DockerContainer dockerContainer = DockerContainer.builder()
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .dockerContainerStatus(DockerContainerStatus.STARTED)
                .endPointUrl("http://localhost:8080")
                .applicationId(applicationId)
                .build();
        DockerContainer stopped = DockerContainer.builder()
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .dockerContainerStatus(DockerContainerStatus.STOPPED)
                .endPointUrl("http://localhost:8080")
                .applicationId(applicationId)
                .build();

        Mockito.when(dockerConnector.stopContainer(Mockito.any())).thenReturn(true);
        Mockito.when(containerRepository.save(Mockito.any(DockerContainer.class)))
                .thenReturn(stopped);
        Mockito.when(containerRepository.findByApplicationId(Mockito.eq(applicationId.getValue())))
                .thenReturn(Optional.of(dockerContainer));

        // when
        deployApplicationService.stopContainer(new WebAppTrackQuery(applicationId.getValue()),
                new UserGlobal(userId.toString(), username));
        // then
        Mockito.verify(containerRepository, Mockito.times(1)).save(Mockito.any(DockerContainer.class));
        Mockito.verify(containerRepository, Mockito.times(1))
                .findByApplicationId(Mockito.eq(applicationId.getValue()));
    }

    @Test
    @DisplayName("track Application Resources")
    public void testTrackApplicationResources() {

        String logs = "liber accommodare metus instructior tacimates veritus maximus ponderum" +
                " dictumst lectus definitiones gloriatur quem dolor commune impetus detraxit" +
                " intellegebat doctus luptatum cu utamur autem alienum vero tantas arcu elit " +
                "pericula potenti expetendis feugait cetero ridiculus eget quaestio facilisi" +
                " invenire sem mel risus diam vel purus tacimates nominavi scripta sapien";
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        UserGlobal userGlobal = new UserGlobal(userId.toString(), username);
        DockerContainer dockerContainer = DockerContainer.builder()
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .dockerContainerStatus(DockerContainerStatus.STOPPED)
                .endPointUrl("http://localhost:8080")
                .applicationId(applicationId)
                .build();
        WebApp webApp = WebApp.builder()
                .applicationId(applicationId)
                .applicationName(new ApplicationName("applicationName"))
                .userId(new UserId(userId.toString()))
                .applicationStatus(ApplicationStatus.COMPLETE)
                .javaVersion(new JavaVersion(17))
                .serverPort(new ServerPort(10020))
                .errorMessages("")
                .build();
        WebAppTrackQuery webAppTrackQuery = WebAppTrackQuery.builder()
                .applicationId(applicationId.getValue()).build();
        ResourceUsage resourceUsage = ResourceUsage.builder().cpuUsage("4.3%").memoryUsage("152MB").build();

        Mockito.when(webAppRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(webApp));
        Mockito.when(containerRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(dockerContainer));
        Mockito.when(dockerConnector.getResourceUsage(Mockito.any())).thenReturn(resourceUsage);
        Mockito.when(dockerConnector.trackLogs(dockerContainer.getDockerContainerId().getValue()))
                .thenReturn(logs);

        // when
        WebAppContainerResponse webAppContainerResponse = deployApplicationService
                .trackQueryDockerContainerResponse(webAppTrackQuery, userGlobal);

        // then
        Assertions.assertNotNull(webAppContainerResponse);
        Assertions.assertEquals(logs, webAppContainerResponse.getLogs());
        Assertions.assertEquals("4.3%", webAppContainerResponse.getCpuUsage());
        Assertions.assertEquals("152MB", webAppContainerResponse.getMemoryUsage());
        Assertions.assertEquals(applicationId.getValue().toString(), webAppContainerResponse.getApplicationId());
    }

    @Test
    @DisplayName("track webapp test")
    public void testTrackWebApp() {
        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        WebAppTrackQuery webAppTrackQuery = WebAppTrackQuery.builder()
                .applicationId(applicationId.getValue())
                .build();
        UserGlobal userGlobal = new UserGlobal(userId.toString(), username);
        DockerContainer dockerContainer = DockerContainer.builder()
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .dockerContainerStatus(DockerContainerStatus.STOPPED)
                .endPointUrl("http://localhost:8080")
                .applicationId(applicationId)
                .build();
        WebApp webApp = WebApp.builder()
                .applicationId(applicationId)
                .applicationName(new ApplicationName("applicationName"))
                .userId(new UserId(userId.toString()))
                .applicationStatus(ApplicationStatus.COMPLETE)
                .javaVersion(new JavaVersion(17))
                .serverPort(new ServerPort(10020))
                .errorMessages("")
                .build();

        Mockito.when(webAppRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(webApp));
        Mockito.when(containerRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(dockerContainer));

        // when
        WebAppTrackResponse webAppTrackResponse = deployApplicationService.trackQueryWebApp(webAppTrackQuery, userGlobal);

        // then
        Assertions.assertNotNull(webAppTrackResponse);
        Assertions.assertEquals(webAppTrackQuery.getApplicationId(), webAppTrackResponse.getApplicationId());
        Assertions.assertEquals(dockerContainer.getEndPointUrl(), webAppTrackResponse.getEndPointUrl());
    }

    @Test
    @DisplayName("S3 업로드 테스트")
    public void testUploadS3() throws IOException {
        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        Storage storage = Storage.createStorage(applicationId);
        StorageInfo storageInfo = StorageInfo.builder()
                .storageName("test.jar")
                .fildUrl("test.jar").build();
        WebApp webApp = WebApp.builder()
                .applicationId(applicationId)
                .build();
        MultipartFile file = new MockMultipartFile("file", "test.jar", "application/octet-stream", new byte[0]);

        Mockito.when(s3Bucket.uploadS3(Mockito.any())).thenReturn(storageInfo);
        Mockito.when(storageRepository.save(Mockito.any(Storage.class))).thenReturn(storage);
        Mockito.when(storageRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(storage));
        // when
        Storage result = storageHandler.uploadS3(webApp.getId().getValue(), file);

        // then
        Assertions.assertEquals(storageInfo.getFildUrl(), result.getStorageUrl());
        Assertions.assertEquals(storageInfo.getStorageName(), result.getStorageName());
        Mockito.verify(storageRepository).save(storage);
    }

    @Test
    @DisplayName("Docker 컨테이너 생성 테스트")
    public void testCreateDockerContainer() {
        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        String fileUrl = "http://s3.aws.com/test.jar";
        DockerContainer dockerContainer = DockerContainer.builder()
                .applicationId(applicationId)
                .endPointUrl("")
                .dockerContainerStatus(DockerContainerStatus.INITIALIZED)
                .dockerContainerId(new DockerContainerId(""))
                .build();
        Storage storage = Storage.builder()
                .applicationId(applicationId)
                .storageUrl(fileUrl)
                .build();
        WebApp webApp = WebApp.builder()
                .applicationId(applicationId)
                .build();
        DockerCreated dockerCreated = new DockerCreated( "dockerContainerId",
                DockerContainerStatus.STARTED,"");

        Mockito.when(dockerConnector.createContainer(webApp, fileUrl)).thenReturn(dockerCreated);
        Mockito.when(containerRepository.save(Mockito.any(DockerContainer.class))).thenReturn(dockerContainer);
        Mockito.when(containerRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(dockerContainer));
        // when
        DockerContainer result = dockerContainerHandler.createDockerImageAndRun(webApp,storage.getStorageUrl());

        // then
        Assertions.assertEquals(DockerContainerStatus.STARTED, result.getDockerContainerStatus());
        Mockito.verify(dockerConnector).createContainer(webApp, fileUrl);
        Mockito.verify(containerRepository).save(dockerContainer);
    }

    @Test
    @DisplayName("Jar 파일 저장 및 컨테이너 생성 전체 테스트")
    public void testSaveJarFileAndCreateContainer() throws IOException {
        // given
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID());
        UserGlobal userGlobal = new UserGlobal(userId.toString(), username);

        // Mock MultipartFile 생성
        MultipartFile file = new MockMultipartFile("file", "test.jar",
                "application/octet-stream", new byte[0]);
        WebAppFileCreateCommand webAppFileCreateCommand =
                new WebAppFileCreateCommand(applicationId.getValue().toString(), file);

        createdFiles.add(applicationId.getValue().toString() + "-test.jar"); // 예시로 추가 (파일 경로에 맞게 수정)
        // WebApp 초기 상태 설정
        WebApp webApp = WebApp.builder()
                .applicationId(applicationId)
                .applicationName(new ApplicationName("applicationName"))
                .userId(new UserId(userId.toString()))
                .applicationStatus(ApplicationStatus.CREATED)
                .javaVersion(new JavaVersion(17))
                .serverPort(new ServerPort(10020))
                .errorMessages("")
                .build();

        // Mock 결과값 정의
        String storageName = "test-storage";
        String fileUrl = "http://s3.aws.com/test.jar";
        Storage storage = Storage.builder()
                .applicationId(applicationId)
                .storageUrl(fileUrl)
                .storageName(storageName)
                .build();
        Storage beforeUploadS3Storage = Storage.builder()
                .applicationId(applicationId)
                .storageUrl("")
                .storageName("")
                .build();
        DockerContainer beforeStartDockerContainer = DockerContainer.builder()
                .dockerContainerStatus(DockerContainerStatus.INITIALIZED)
                .applicationId(applicationId)
                .endPointUrl("")
                .dockerContainerId(new DockerContainerId(""))
                .build();
        DockerContainer dockerContainer = DockerContainer.builder()
                .dockerContainerStatus(DockerContainerStatus.STARTED)
                .applicationId(applicationId)
                .endPointUrl("http://localhost:8080")
                .dockerContainerId(new DockerContainerId("dockerContainerId"))
                .build();
        // Repository 및 Connector Mock 설정
        Mockito.when(webAppRepository.findByApplicationId(applicationId.getValue()))
                .thenReturn(Optional.of(webApp));
        Mockito.when(storageRepository.findByApplicationId(applicationId.getValue()))
                        .thenReturn(Optional.of(beforeUploadS3Storage));
        Mockito.when(s3Bucket.uploadS3(Mockito.any())).
                thenReturn(StorageInfo.builder()
                        .fildUrl(fileUrl).storageName(storageName).build());

        Mockito.when(storageRepository.save(Mockito.any(Storage.class))).thenReturn(storage);

        Mockito.when(containerRepository.findByApplicationId(applicationId.getValue())
        ).thenReturn(Optional.of(beforeStartDockerContainer));

        Mockito.when(dockerConnector.createContainer(webApp, storage.getStorageUrl()))
                .thenReturn(DockerCreated.builder()
                        .dockerContainerStatus(DockerContainerStatus.STARTED).
                        dockerContainerId("dockerContainerId").build());
        Mockito.when(containerRepository.save(Mockito.eq(beforeStartDockerContainer)))
                .thenReturn(dockerContainer);

        // when
        deployApplicationService.saveJarFile(webAppFileCreateCommand, userGlobal);

        // then

        Assertions.assertEquals(ApplicationStatus.COMPLETE, webApp.getApplicationStatus());
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String filename = String.format("%s-%s", UUID.randomUUID(), multipartFile.getOriginalFilename());
        File convertedFile = new File(filename);

        if (convertedFile.createNewFile()) {
            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        }
        return convertedFile;
    }
}
