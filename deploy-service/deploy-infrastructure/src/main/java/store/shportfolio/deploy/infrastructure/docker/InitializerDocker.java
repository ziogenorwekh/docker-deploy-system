package store.shportfolio.deploy.infrastructure.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Image;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerfileCreateHelper;

import java.util.List;

@Slf4j
@Service
public class InitializerDocker {

    private final DockerClient dockerClient;

    public InitializerDocker(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @PostConstruct
    public void init() {
        log.warn("Initializing Docker client");

        try {
            // Docker 서버와 연결이 가능한지 ping을 통해 확인
            dockerClient.pingCmd().exec();
            log.info("Docker client is successfully connected!");

            // Docker 이미지 목록 가져오기
            List<Image> images = dockerClient.listImagesCmd().exec();

            if (images.isEmpty()) {
                log.info("No Docker images found.");
            } else {
                log.info("Docker images currently available:");
                for (Image image : images) {
                    log.info("Image ID: {}, Repo Tags: {}", image.getId(), String.join(", ", image.getRepoTags()));
                }
            }


        } catch (DockerException e) {
            log.error("Docker client failed to connect: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while connecting to Docker: {}", e.getMessage());
        }
    }

}
