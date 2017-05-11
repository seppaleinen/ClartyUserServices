package se.claremont.backend.user.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

    @GetMapping(path = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<String> ping() {
        return ResponseEntity.ok("Hello");
    }
    
    @RequestMapping("/")
    String hello() {
        return "hello world";
    }

    /* Maps to all HTTP actions by default (GET,POST,..)*/
    @RequestMapping("/users")
    public @ResponseBody String getUsers() {
      return "{\"users\":[{\"firstname\":\"Richard\", \"lastname\":\"Feynman\"}," +
             "{\"firstname\":\"Marie\",\"lastname\":\"Curie\"}]}";
    }
    
}
