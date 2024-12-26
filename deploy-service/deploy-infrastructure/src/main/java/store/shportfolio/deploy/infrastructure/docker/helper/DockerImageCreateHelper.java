package store.shportfolio.deploy.infrastructure.docker.helper;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.DockerContainerException;
import store.shportfolio.deploy.domain.entity.WebApp;

import java.io.File;
import java.util.Set;

@Slf4j
@Component
public class DockerImageCreateHelper {

    private final DockerClient dockerClient;

    public DockerImageCreateHelper(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String createImage(WebApp webApp, File dockerfile) {
        try {
            // Build context 경로 (Dockerfile의 위치가 포함된 디렉토리)
            log.debug("file path : {}", dockerfile.getAbsolutePath());
            File buildContext = dockerfile.getAbsoluteFile();

            // 이미지 이름 정의
            String imageName = String.format("%s:%s", webApp.getApplicationName().getValue(), "latest");

            // Docker 이미지 빌드
            String imageId = dockerClient.buildImageCmd(buildContext)
                    .withDockerfile(dockerfile)
                    .withTags(Set.of(imageName)) // 이미지 이름 태그 추가
                    .exec(new BuildImageResultCallback())
                    .awaitImageId(); // 이미지 생성 결과의 ID 반환

            return imageId;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DockerContainerException("Error causes creating Docker image.", e);
        }
    }
}
