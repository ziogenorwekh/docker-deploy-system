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
    private final String EXPOSE;
    private final String CMD;

    @Builder
    public DockerfileCommand(Integer jdkVersion, String s3URL, String applicationName, Integer serverPort) {
        // Alpine 기반 openjdk 이미지 사용
        this.RUN_2 = String.format("RUN apk add openjdk%s-jre", jdkVersion);
        this.RUN_3 = String.format("RUN curl --max-time 600 -o %s.jar %s", applicationName, s3URL);
        this.EXPOSE = String.format("EXPOSE %s", serverPort); // 서버 포트 노출
        this.CMD = String.format("RUN java -jar %s.jar", applicationName);// S3에서 파일을 다운로드
    }

    public String writeDockerfileCommand() {
        List<String> writer = new ArrayList<>();
        writer.add(FIRST);
        writer.add(RUN_1); // apk 명령어 추가
        writer.add(RUN_2);
        writer.add(RUN_3);
        writer.add(EXPOSE);
        writer.add(CMD);

        return String.join("\n", writer);
    }
}