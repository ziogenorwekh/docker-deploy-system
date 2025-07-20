package store.shportfolio.deploy.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class WebAppFileCreateCommand {

    private String applicationId;
    private MultipartFile file;
}
