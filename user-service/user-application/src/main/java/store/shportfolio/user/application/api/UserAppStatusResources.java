package store.shportfolio.user.application.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/status")
public class UserAppStatusResources {

    @GetMapping
    public ResponseEntity<Void> status() {
        log.debug("UserAppStatusResources.status");
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
