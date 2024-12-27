package store.shportfolio.deploy.infrastructure.docker.helper;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DockerfileCommand {
    private final String FIRST = "FROM alpine:latest";
    private final String RUN_1 = "RUN apk update && apk add curl";
    private final String RUN_2;
    private final String RUN_3;
    private final String RUN_4;
    private final String RUN_5;
    private final String EXPOSE;

    @Builder
    public DockerfileCommand(Integer jdkVersion, String s3URL, String applicationName, Integer serverPort) {
        // Alpine 기반 openjdk 이미지 사용
        this.RUN_2 = String.format("RUN apk add --no-cache openjdk%s-jre curl bash", jdkVersion);
        this.RUN_3 = String.format("WORKDIR /app");
        this.RUN_4 = String.format("RUN curl --max-time 600 -o %s.jar %s", applicationName, s3URL);
        this.RUN_5 = String.format("RUN chmod +x /app/%s.jar", applicationName);
        this.EXPOSE = String.format("EXPOSE %s", serverPort); // 서버 포트 노출
    }

    public String writeDockerfileCommand() {
        List<String> writer = new ArrayList<>();
        writer.add(FIRST);
        writer.add(RUN_1); // apk 명령어 추가
        writer.add(RUN_2);
        writer.add(RUN_3);
        writer.add(RUN_4);
        writer.add(RUN_5);
        writer.add(EXPOSE);

        return String.join("\n", writer);
    }
}