package juja.microservices.gamification.slackbot.dao;

import juja.microservices.gamification.slackbot.exceptions.UserExchangeException;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Artem
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestUserRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    private UserRepository userRepository;
    @Inject
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Value("${users.endpoint.usersBySlackNames}")
    private String usersFindUsersBySlackNamesUrl;
    @Value("${users.endpoint.usersByUuids}")
    private String usersFindUsersByUuidsUrl;

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void shouldReturnListUserDTOWhenSendSlackNameList() {
        //given
        List<String> slackNames = new ArrayList<>();
        slackNames.add("bob.slack");
        slackNames.add("@john.slack");
        mockServer.expect(requestTo(usersFindUsersBySlackNamesUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"slackNames\":[\"@bob.slack\",\"@john.slack\"]}"))
                .andRespond(withSuccess("[{\"uuid\":\"AAAA123\",\"slack\":\"@bob.slack\"}, " +
                        "{\"uuid\":\"AAAA321\",\"slack\":\"@john.slack\"}]", MediaType.APPLICATION_JSON_UTF8));
        //when
        List<UserDTO> result = userRepository.findUsersBySlackNames(slackNames);
        // then
        mockServer.verify();
        assertEquals("[UserDTO(uuid=AAAA123, slack=@bob.slack), UserDTO(uuid=AAAA321, slack=@john.slack)]",
                result.toString());
    }

    @Test
    public void shouldReturnListUserDTOWhenSendUuidsSet() {
        //given
        Set<String> uuids = new LinkedHashSet<>(Arrays.asList("uuid1", "uuid2"));
        mockServer.expect(requestTo(usersFindUsersByUuidsUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"uuids\":[\"uuid1\",\"uuid2\"]}"))
                .andRespond(withSuccess("[{\"uuid\":\"uuid1\",\"slack\":\"@bob.slack\"}, " +
                        "{\"uuid\":\"uuid2\",\"slack\":\"@john.slack\"}]", MediaType.APPLICATION_JSON_UTF8));
        //when
        Set<UserDTO> result = userRepository.findUsersByUuids(uuids);
        // then
        mockServer.verify();
        assertEquals("[UserDTO(uuid=uuid1, slack=@bob.slack), UserDTO(uuid=uuid2, slack=@john.slack)]",
                result.toString());
    }

    @Test
    public void shouldThrowExceptionWhenUserRepositoryThrowException() {
        //given
        mockServer.expect(requestTo(usersFindUsersByUuidsUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body("bad request"));

        //then
        thrown.expect(UserExchangeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        //when
        userRepository.findUsersByUuids(new HashSet<>(Arrays.asList("uuid1", "uuid2")));
    }
}