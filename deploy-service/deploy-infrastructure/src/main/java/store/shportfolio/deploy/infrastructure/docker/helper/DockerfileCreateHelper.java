package store.shportfolio.deploy.infrastructure.docker.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.domain.entity.WebApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class DockerfileCreateHelper {

    @Value("${dockerfile.root.directory}")
    private String filePath;

    public File createDockerfile(WebApp webApp, String storageUrl) {
        String outputFile = String.format("%s/Dockerfile-%s", filePath, webApp.getApplicationName().getValue());
        DockerfileCommand dockerfileCommand = new DockerfileCommand(webApp.getJavaVersion().getVersion(), storageUrl
                , webApp.getApplicationName().getValue(), webApp.getServerPort().getValue());
        File dockerfile = null;
        try {
            dockerfile = new File(outputFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(dockerfile));
            writer.write(dockerfileCommand.writeDockerfileCommand());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            log.error("create dockerfile error message is : {}", e.getMessage());
        }
        return dockerfile;
    }

    public void deleteLocalDockerfile(File dockerfile) {
        if (dockerfile.delete()) {
            log.info("file deleted by name is : {}", dockerfile.getName());
        }
    }
}
