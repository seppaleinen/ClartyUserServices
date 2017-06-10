package se.claremont.backend.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/ping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody String ping() {
        return "Hello";
    }
    
    @RequestMapping("/")
    @ResponseBody String hello() {
        return "hello world";
    }

    /* Maps to all HTTP actions by default (GET,POST,..)*/
    @RequestMapping(value = "/users")
    @ResponseBody List<String> getUsers() {
        return userRepository.findAll()
                        .stream()
                        .map(User::getUsername)
                        .collect(Collectors.toList());
    }
    
}
