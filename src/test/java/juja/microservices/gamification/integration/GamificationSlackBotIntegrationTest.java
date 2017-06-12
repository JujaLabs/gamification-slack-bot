package juja.microservices.gamification.integration;

import juja.microservices.gamification.slackbot.controller.GamificationSlackCommandController;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.UserService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class GamificationSlackBotIntegrationTest {

    private final String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";

    @Inject
    private GamificationService gamificationService;
    @Inject
    private UserService userService;
    @Inject
    private SlackNameHandlerService slackNameHandlerService;
    @Inject
    private RestTemplate restTemplate;

    @Inject
    private MockMvc mvc;
    private MockRestServiceServer mockServer;

    @Value("${gamification.baseURL}")
    private String urlBaseGamification;
    @Value("${endpoint.daily}")
    private String urlSendDaily;
    @Value("${endpoint.codenjoy}")
    private String urlSendCodenjoy;
    @Value("${endpoint.thanks}")
    private String urlSendThanks;
    @Value("${endpoint.interview}")
    private String urlSendInterview;

    @Value("${user.baseURL}")
    private String urlBaseUser;
    @Value("${endpoint.userSearch}")
    private String urlGetUser;

    final List<UserDTO> USERS = new ArrayList<>();
    {
        USERS.add(new UserDTO("f2034f11-561a-4e01-bfcf-ec615c1ba61a","@from-user"));
        USERS.add(new UserDTO("f2034f22-562b-4e02-bfcf-ec615c1ba62b","@slack1"));
        USERS.add(new UserDTO("f2034f33-563c-4e03-bfcf-ec615c1ba63c","@slack2"));
        USERS.add(new UserDTO("f2034f44-563d-4e04-bfcf-ec615c1ba64d","@slack3"));
    }


    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
        final String CODENJOY_COMMAND_FROM_SLACK = "-1th @slack1 -2th @slack2 -3th @slack3";
        mockUsersService(USERS);

        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}",
                USERS.get(0).getUuid(), USERS.get(1).getUuid(), USERS.get(2).getUuid(), USERS.get(3).getUuid());

        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\", \"102\", \"103\"]";

        mockGamificationService(USERS, EXPECTED_REQUEST_TO_GAMIFICATION, EXPECTED_RESPONSE_FROM_GAMIFICATION);

        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, we awarded the users.";

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate("/commands/codenjoy"),
                getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_FROM_SLACK))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
    }

    private void mockUsersService(List<UserDTO> users) {
        for (UserDTO user : users) {
            mockServer.expect(requestTo(urlBaseUser + urlGetUser))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(String.format("{\"slackNames\":[\"%s\"]}", user.getSlack())))
                    .andRespond(withSuccess(String.format("[{\"uuid\":\"%s\",\"slack\":\"%s\"}]",
                            user.getUuid(), user.getSlack()), MediaType.APPLICATION_JSON_UTF8));
        }
    }

    private void mockGamificationService(List<UserDTO> users, String expectedRequestBody, String response){
        mockServer.expect(requestTo(urlBaseGamification + urlSendCodenjoy))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString("application/json")))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

    }


    private String getUrlTemplate(String endpoint) {
        return endpoint + "?" +
                "token={slashCommandToken}&" +
                "team_id={team_id}&" +
                "team_domain={team_domain}&" +
                "channel_id={channel_id}&" +
                "channel_name={channel_name}&" +
                "user_id={user_id}&" +
                "user_name={user_name}&" +
                "command={command}&" +
                "text={text}&" +
                "response_url={response_url}&";
    }

    private Object[] getUriVars(String slackToken, String command, String description) {
        return new Object[]{slackToken,
                "any_team_id",
                "any_domain",
                "UHASHB8JB",
                "test-channel",
                "UNJSD9OKM",
                "@from-user",
                command,
                description,
                "http://example.com"};
    }
}
