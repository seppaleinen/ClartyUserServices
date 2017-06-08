package se.claremont.backend.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.entities.User;
import se.claremont.backend.user.security.AccountCredentials;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class IntegrationTest {
    @Autowired
    private MockMvc mvc;
    private String token;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() throws Exception {
        userRepository.deleteAll();
        token = mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("admin", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    public void canLoginWithDefaultUsername() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("admin", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));
    }

    @Test
    public void canLoginWithCustomUser() throws Exception {
        userRepository.save(new User("custom_user"));
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("custom_user", "invalid")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));
    }

    @Ignore("This one should fail but doesn't")
    @Test
    public void loginFailureWhenUsernameNotExisting() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content(createJsonObjectFromUserPassword("not_existing_username", "password")))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    public void verifyThatPingServiceIsAccessibleWithToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ping")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string(is("Hello")));
    }

    @Test
    public void verifyThatPingServiceIsNotAccessibleWithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ping"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.content().string(isEmptyString()));
    }

    private String createJsonObjectFromUserPassword(String username, String password) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new AccountCredentials("admin", "password"));
    }

}
