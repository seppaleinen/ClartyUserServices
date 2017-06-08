package se.claremont.backend.user;

import org.junit.Before;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.claremont.backend.user.security.WebSecurityConfig;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
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
    private WebSecurityConfig webSecurityConfig;

    @Before
    public void setup() throws Exception {
        token = mvc.perform(MockMvcRequestBuilders.post("/login")
                .content("{\"username\":\"admin\",\"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    public void canLoginWithDefaultUsername() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                .content("{\"username\":\"admin\",\"password\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.AUTHORIZATION, not(isEmptyOrNullString())));
    }

    @Test
    public void canLoginWithCustomUser() {

    }

    @Test
    public void verifyThatPingServiceIsAccessible() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/ping").header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.content().string(is("Hello")));
    }

}
