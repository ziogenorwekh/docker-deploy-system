package store.shportfolio.database.application.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/status")
public class DatabaseAppStatusResources {

    @GetMapping
    public ResponseEntity<Void> status() {
        log.debug("DatabaseAppStatusResources.status");
        return ResponseEntity.ok().build();
    }
}
