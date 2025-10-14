package store.shportfolio.deploy.infrastructure.docker.helper;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DockerfileCommand {

    private final String FIRST = "FROM alpine:latest";
    private final String RUN_SETUP_AND_JDK;
    private final String WORKDIR;
    private final String CURL_DOWNLOAD;
    private final String EXPOSE;
    private final String ENTRYPOINT;

    @Builder
    public DockerfileCommand(Integer jdkVersion, String s3URL, String applicationName, Integer serverPort) {

        this.RUN_SETUP_AND_JDK = String.format(
                "RUN apk update && " +
                        "apk add --no-cache openjdk%s-jre curl bash && " +
                        "rm -rf /var/cache/apk/*",
                jdkVersion
        );

        this.WORKDIR = "WORKDIR /app";

        this.CURL_DOWNLOAD = String.format(
                "RUN curl --max-time 600 -o /app/%s.jar %s",
                applicationName,
                s3URL
        );

        this.EXPOSE = String.format("EXPOSE %s", serverPort);

        this.ENTRYPOINT = String.format(
                "ENTRYPOINT [\"java\", \"-jar\", \"/app/%s.jar\"]",
                applicationName
        );
    }

    public String writeDockerfileCommand() {
        List<String> writer = new ArrayList<>();
        writer.add(FIRST);
        writer.add(RUN_SETUP_AND_JDK);
        writer.add(WORKDIR);
        writer.add(CURL_DOWNLOAD);
        writer.add(EXPOSE);
        writer.add(ENTRYPOINT);

        return String.join("\n", writer);
    }
}