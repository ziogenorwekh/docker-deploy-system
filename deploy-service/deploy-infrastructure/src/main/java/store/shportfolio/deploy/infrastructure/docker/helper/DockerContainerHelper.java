package store.shportfolio.deploy.infrastructure.docker.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.DockerContainerException;
import store.shportfolio.deploy.domain.entity.WebApp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DockerContainerHelper {

    private final DockerClient dockerClient;

    public DockerContainerHelper(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    /**
     * 이미지 기반으로 컨테이너를 실행합니다.
     *
     * @param imageId Docker 이미지 ID
     * @param webApp  WebApp 엔티티
     * @return 생성된 컨테이너 ID
     */
    public String runContainer(String imageId, WebApp webApp) {
        try {
            // WebApp에서 필요한 포트 정보 가져오기
            int exposedPort = webApp.getServerPort().getValue();
            ExposedPort tcpPort = ExposedPort.tcp(exposedPort);

            // 호스트와 컨테이너 간의 포트 매핑 설정
            Ports portBindings = new Ports();

            portBindings.bind(tcpPort, Ports.Binding.bindPort(exposedPort));

            // 컨테이너 생성
            String containerName = webApp.getApplicationName().getValue();
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageId)
                    .withName(containerName)
                    .withExposedPorts(tcpPort)
                    .withStdinOpen(true)
                    .withTty(true)
                    .withEntrypoint("java", "-jar", containerName + ".jar")
                    .withPortBindings(portBindings)
                    .exec();

            String containerId = containerResponse.getId();
            // 컨테이너 시작
            dockerClient.startContainerCmd(containerId).exec();

            return containerId;

        } catch (DockerException e) {
            log.error("DockerContainerHelper: {}", e.getMessage());
            throw new DockerContainerException("Error running container", e);
        }
    }

    /**
     * 컨테이너 ID를 기반으로 컨테이너를 시작합니다.
     *
     * @param containerId Docker 컨테이너 ID
     */
    public Boolean startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            return true;
        } catch (DockerException e) {
            log.error("DockerContainerHelper: {}", e.getMessage());
            throw new DockerContainerException("Error caused by starting container.", e);
        }
    }

    /**
     * 컨테이너 ID를 기반으로 컨테이너를 중지합니다.
     *
     * @param containerId Docker 컨테이너 ID
     */
    public Boolean stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            return true;
        } catch (DockerException e) {
            log.error("DockerContainerHelper: {}", e.getMessage());
            throw new DockerContainerException("Error caused by stopping container.", e);
        }
    }


    public String trackLogContainer(String containerId) {
        if (!isContainerRunning(containerId)) {
            throw new DockerContainerException("Container is not running");
        }

        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            // 로그를 받아서 처리하는 callback 정의
            LogContainerResultCallback callback = new LogContainerResultCallback() {
                @Override
                public void onNext(com.github.dockerjava.api.model.Frame item) {
                    try {
                        // 출력 스트림에 로그 추가
                        outputStream.write(item.getPayload());
                    } catch (Exception e) {
                        log.error("DockerContainerHelper: {}", e.getMessage());
                        throw new DockerContainerException("Error writing log frame", e);
                    }
                }

                @Override
                public void onComplete() {
                    super.onComplete();
                }
            };

            // 로그 가져오기 작업 시작
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true) // 표준 출력 로그 가져오기
                    .withStdErr(true) // 표준 에러 로그 가져오기
                    .withFollowStream(false) // 실시간 스트리밍 비활성화
                    .withTailAll() // 끝까지 로그 가져오기
                    .exec(callback);

            callback.awaitCompletion();
            return outputStream.toString();

        } catch (Exception e) {
            log.error("DockerContainerHelper: {}", e.getMessage());
            throw new DockerContainerException("Error fetching container logs", e);
        }
    }


    public void dropContainerAndRemoveImage(String containerId, String imageId) {
        try {
            if (isContainerRunning(containerId)) {
                dockerClient.stopContainerCmd(containerId).exec();
            }
            dockerClient.removeContainerCmd(containerId).exec();
            dockerClient.removeImageCmd(imageId).exec();
        } catch (Exception e) {
            log.error("DockerContainerHelper: {}", e.getMessage());
            throw new DockerContainerException("Error fetching container logs", e);
        }
    }

    private boolean isContainerRunning(String containerId) {
        try {
            InspectContainerResponse exec = dockerClient.inspectContainerCmd(containerId).exec();
            return Boolean.TRUE.equals(exec.getState().getRunning());
        } catch (Exception e) {
            throw new DockerContainerException("Error checking container status: " + e.getMessage());
        }
    }
}
