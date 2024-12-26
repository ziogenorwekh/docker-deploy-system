package store.shportfolio.deploy.application.command;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class WebAppFileCreateCommand {

    @Setter(AccessLevel.PUBLIC)
    private String applicationId;
    private MultipartFile file;
}
