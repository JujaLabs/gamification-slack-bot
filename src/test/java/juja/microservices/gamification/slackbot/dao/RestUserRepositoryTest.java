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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Artem
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
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

    @Value("${users.endpoint.findUsersBySlackIds}")
    private String urlFindUsersBySlackUsers;

    @Value("${users.endpoint.findUsersByUuids}")
    private String urlFindUsersByUuids;

    private static final String SLACK_USER1="slack1";
    private static final String SLACK_USER2="slack2";

    private UserDTO user1;
    private UserDTO user2;

    @Before
    public void setup() {
        user1 = new UserDTO("uuid1", SLACK_USER1);
        user2 = new UserDTO("uuid2", SLACK_USER2);
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void shouldReturnListUserDTOWhenSendSlackUsersList() {
        //given
        List<String> slackUsers = new ArrayList<>();
        slackUsers.add(SLACK_USER1);
        slackUsers.add(SLACK_USER2);

        String expectedRequestContent = String.format("{\"slackIds\":[\"%s\",\"%s\"]}", SLACK_USER1, SLACK_USER2);

        String responseBody = String.format("[{\"uuid\":\"%s\",\"slackId\":\"%s\"}, {\"uuid\":\"%s\",\"slackId\":\"%s\"}]",
                user1.getUuid(), user1.getSlackUser(),user2.getUuid(), user2.getSlackUser());

        mockServer.expect(requestTo(urlFindUsersBySlackUsers))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(expectedRequestContent))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON_UTF8));

        //when
        List<UserDTO> result = userRepository.findUsersBySlackUsers(slackUsers);

        // then
        mockServer.verify();

        assertThat(result,hasSize(2));
        assertThat(result,hasItem(user1));
        assertThat(result,hasItem(user2));
    }

    @Test
    public void shouldReturnListUserDTOWhenSendUuidsSet() {
        //given
        Set<String> uuids = new LinkedHashSet<>(Arrays.asList(user1.getUuid(), user2.getUuid()));

        String expectedRequestContent = String.format("{\"uuids\":[\"%s\",\"%s\"]}", user1.getUuid(),user2.getUuid());

        String responseBody = String.format("[{\"uuid\":\"%s\",\"slackId\":\"%s\"}, {\"uuid\":\"%s\",\"slackId\":\"%s\"}]",
                user1.getUuid(), user1.getSlackUser(),user2.getUuid(), user2.getSlackUser());

        mockServer.expect(requestTo(urlFindUsersByUuids))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(expectedRequestContent))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON_UTF8));
        //when
        Set<UserDTO> result = userRepository.findUsersByUuids(uuids);

        // then
        mockServer.verify();
        assertThat(result,hasSize(2));
        assertThat(result,hasItem(user1));
        assertThat(result,hasItem(user2));
    }

    @Test
    public void shouldThrowExceptionWhenUserRepositoryThrowException() {
        //given
        mockServer.expect(requestTo(urlFindUsersByUuids))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body("bad request"));

        //then
        thrown.expect(UserExchangeException.class);
        thrown.expectMessage(containsString("I'm, sorry. I cannot parse api error message from remote service :("));

        //when
        userRepository.findUsersByUuids(new HashSet<>(Arrays.asList("uuid1", "uuid2")));
    }
}