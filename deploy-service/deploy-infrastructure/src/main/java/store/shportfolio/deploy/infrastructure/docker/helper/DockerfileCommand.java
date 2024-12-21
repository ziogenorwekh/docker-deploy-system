package store.shportfolio.deploy.infrastructure.docker.helper;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DockerfileCommand {
    private final String FIRST;
    private final String RUN_1 = "RUN apk update && apk add curl";
    private final String RUN_2;
    private final String RUN_3;
    private final String EXPOSE;

    @Builder
    public DockerfileCommand(Integer jdkVersion, String s3URL, String applicationName, Integer serverPort) {
        this.FIRST = String.format("FROM openjdk:%s", jdkVersion); // 변경된 부분
        this.RUN_2 = String.format("RUN curl -o %s.jar %s", applicationName, s3URL);
        this.RUN_3 = String.format("RUN chmod +x %s.jar", applicationName); // 실행 권한 추가
        this.EXPOSE = String.format("EXPOSE %s", serverPort);
    }

    public String writeDockerfileCommand() {
        List<String> writer = new ArrayList<>();
        writer.add(FIRST);
        writer.add(RUN_1);
        writer.add(RUN_2);
        writer.add(RUN_3);
        writer.add(EXPOSE);

        return String.join("\n", writer);
    }
}