package se.claremont.backend.user.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
class UserController {

    @GetMapping(path = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<String> ping() {
        return ResponseEntity.ok("Hello");
    }

}
