package juja.microservices.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import juja.microservices.gamification.slackbot.GamificationSlackBotApplication;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInSlackFormat;
import static juja.microservices.utils.SlackUtils.getUriVars;
import static juja.microservices.utils.SlackUtils.getUrlTemplate;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GamificationSlackBotApplication.class})
@AutoConfigureMockMvc
public class GamificationSlackBotIntegrationTest {
    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String CODENJOY_THANKS_MESSAGE = "Thanks, we awarded the users. " +
            "First place: %s, Second place: %s, Third place: %s";
    private final static String DAILY_THANKS_MESSAGE = "Thanks, your daily report saved.";
    private final static String THANKS_ONE_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved.";
    private final static String INTERVIEW_THANKS_MESSAGE = "Thanks. Your interview saved.";
    private final static String TEAM_THANKS_MESSAGE =
            "Thanks, your team report saved. Members: [%s, %s, %s, %s]";
    private final static String responseUrl = "http://example.com";


    @Inject
    private RestTemplate restTemplate;

    @Inject
    private MockMvc mvc;
    private MockRestServiceServer mockServer;

    private String gamificationSlackbotDailyUrl = "/v1/commands/daily";
    private String gamificationSlackbotThanksUrl = "/v1/commands/thanks";
    private String gamificationSlackbotCodenjoyUrl = "/v1/commands/codenjoy";
    private String gamificationSlackbotInterviewUrl = "/v1/commands/interview";
    private String gamificationSlackbotTeamUrl = "/v1/commands/team";

    @Value("${gamification.endpoint.daily}")
    private String gamificationDailyUrl;
    @Value("${gamification.endpoint.codenjoy}")
    private String gamificationCodenjoyUrl;
    @Value("${gamification.endpoint.thanks}")
    private String gamificationThanksUrl;
    @Value("${gamification.endpoint.interview}")
    private String gamificationInterviewUrl;
    @Value("${gamification.endpoint.team}")
    private String gamificationTeamUrl;

    @Value("${users.endpoint.findUsersBySlackIds}")
    private String usersFindUsersBySlackUsersUrl;
    @Value("${users.endpoint.findUsersByUuids}")
    private String usersFindUsersByUuidsUrl;

    @Value("${teams.endpoint.teamByUserUuid}")
    private String teamGetTeamByUserUuidUrl;

    private static final String SLACK_USER_1 = "slack1";
    private static final String SLACK_USER_2 = "slack2";
    private static final String SLACK_USER_3 = "slack3";
    private static final String SLACK_USER_FROM = "UNJSD9OKM";

    private UserDTO user1 = new UserDTO("f2034f22-562b-4e02-bfcf-ec615c1ba62b", SLACK_USER_1);
    private UserDTO user2 = new UserDTO("f2034f33-563c-4e03-bfcf-ec615c1ba63c", SLACK_USER_2);
    private UserDTO user3 = new UserDTO("f2034f44-563d-4e04-bfcf-ec615c1ba64d", SLACK_USER_3);
    private UserDTO userFrom = new UserDTO("f2034f11-561a-4e01-bfcf-ec615c1ba61a", SLACK_USER_FROM);

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
        String codenjoyCommandFromSlack = String.format("-1th %s -2th %s -3th %s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);

        String expectedRequestToGamification = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}", userFrom.getUuid(),
                user1.getUuid(), user2.getUuid(), user3.getUuid());

        String expectedResponseFromGamification = "[\"101\", \"102\", \"103\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationCodenjoyUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                convertSlackUserInSlackFormat(user1.getSlackUser()),
                convertSlackUserInSlackFormat(user2.getSlackUser()),
                convertSlackUserInSlackFormat(user3.getSlackUser()))
                )
        );

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensWithoutSpaces() throws Exception {
        String codenjoyCommandFromSlack = String.format("-1th%s-2th%s-3th%s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);

        String expectedRequestToGamification = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}", userFrom.getUuid(),
                user1.getUuid(), user2.getUuid(), user3.getUuid());

        String expectedResponseFromGamification = "[\"101\", \"102\", \"103\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationCodenjoyUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                convertSlackUserInSlackFormat(user1.getSlackUser()),
                convertSlackUserInSlackFormat(user2.getSlackUser()),
                convertSlackUserInSlackFormat(user3.getSlackUser())))
        );

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnOkMessageIfCondejoyCommandTokensInWrongOrder() throws Exception {
        String codenjoyCommandFromSlack = String.format("-2th %s -1th %s -3th %s",
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user2, user1, user3, userFrom);

        String expectedRequestToGamification = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}", userFrom.getUuid(),
                user1.getUuid(), user2.getUuid(), user3.getUuid());

        String expectedResponseFromGamification = "[\"101\", \"102\", \"103\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationCodenjoyUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(CODENJOY_THANKS_MESSAGE,
                convertSlackUserInSlackFormat(user1.getSlackUser()),
                convertSlackUserInSlackFormat(user2.getSlackUser()),
                convertSlackUserInSlackFormat(user3.getSlackUser())))
        );

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnErrorMessageIfCondejoyCommandWithout2thToken() throws Exception {
        String codenjoyCommandFromSlack = String.format("-1th %s %s -3th %s",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, user3, userFrom);

        String expectedResponseToSlack = String.format("Token '-2th' didn't find in the string '-1th %s %s -3th %s'",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()),
                convertSlackUserInFullSlackFormat(user3.getSlackUser()));

        mockSuccessUsersService(usersInCommand);

        mockSlackResponseUrl(responseUrl, new RichMessage(expectedResponseToSlack));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotCodenjoyUrl),
                getUriVars("slashCommandToken", "/codenjoy", codenjoyCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
        String dailyCommandFromSlack = "I did smth today";
        List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        String expectedRequestToGamification = String.format("{\"description\":\"%s\",\"from\":\"%s\"}",
                dailyCommandFromSlack, userFrom.getUuid());

        String expectedResponseFromGamification = "[\"101\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationDailyUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(DAILY_THANKS_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotDailyUrl),
                getUriVars("slashCommandToken", "/codenjoy", dailyCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        this.mockServer.verify();
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        String interviewCommandFromSlack = "I went to an interview yesterday and got offer";
        List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        String expectedRequestToGamification = String.format("{\"description\":\"%s\",\"from\":\"%s\"}",
                interviewCommandFromSlack, userFrom.getUuid());

        String expectedResponseFromGamification = "[\"101\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationInterviewUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(INTERVIEW_THANKS_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotInterviewUrl),
                getUriVars("slashCommandToken", "/interview", interviewCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
        String thanksCommandFromSlack = String.format("%s thanks for your help!",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, userFrom);

        String expectedRequestToGamification = String.format("{\"description\":\"%s\",\"from\":\"%s\",\"to\":\"%s\"}",
                "thanks for your help!", userFrom.getUuid(), user1.getUuid());

        String expectedResponseFromGamification = "[\"101\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessPostService(gamificationThanksUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(THANKS_ONE_THANKS_MESSAGE,
                convertSlackUserInSlackFormat(user1.getSlackUser())))
        );

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotThanksUrl),
                getUriVars("slashCommandToken", "/thanks", thanksCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnErrorMessageIfThanksCommandConsistTwoOrMoreSlackUsers() throws Exception {

        String thanksCommandFromSlack = String.format("%s thanks %s for your help!",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, userFrom);


        final String expectedResponseToSlack = String.format("We found 2 slack user in your command: '%s thanks " +
                        "%s for your help!'  You can't send thanks more than one user.",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()));

        mockSuccessUsersService(usersInCommand);

        mockSlackResponseUrl(responseUrl, new RichMessage(expectedResponseToSlack));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotThanksUrl),
                getUriVars("slashCommandToken", "/thanks", thanksCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void onReceiveSlashCommandTeamReturnOkRichMessage() throws Exception {

        List<UserDTO> usersInCommand = Collections.singletonList(userFrom);

        String expectedResponseFromTeam = String.format("{\"members\":[\"%s\", \"%s\", \"%s\", \"%s\"]}",
                userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid());

        String expectedRequestToUserByUuids = String.format("{\"uuids\":[\"%s\",\"%s\",\"%s\",\"%s\"]}",
                userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid()
        );

        String expectedResponseFromUserByUuids = String.format("[{\"uuid\":\"%s\",\"slackId\":\"%s\"}," +
                        "{\"uuid\":\"%s\",\"slackId\":\"%s\"}," +
                        "{\"uuid\":\"%s\",\"slackId\":\"%s\"}," +
                        "{\"uuid\":\"%s\",\"slackId\":\"%s\"}]",
                userFrom.getUuid(), userFrom.getSlackUser(),
                user1.getUuid(), user1.getSlackUser(),
                user2.getUuid(), user2.getSlackUser(),
                user3.getUuid(), user3.getSlackUser()
        );

        String expectedRequestToGamification = String.format("{\"from\":\"%s\",\"members\":[\"%s\",\"%s\",\"%s\",\"%s\"]}",
                userFrom.getUuid(), userFrom.getUuid(), user1.getUuid(), user2.getUuid(), user3.getUuid());

        String expectedResponseFromGamification = "[\"101\", \"102\", \"103\", \"104\"]";

        mockSuccessUsersService(usersInCommand);

        mockSuccessGetService(teamGetTeamByUserUuidUrl + "/" + userFrom.getUuid(), expectedResponseFromTeam);

        mockSuccessPostService(usersFindUsersByUuidsUrl, expectedRequestToUserByUuids,
                expectedResponseFromUserByUuids);

        mockSuccessPostService(gamificationTeamUrl, expectedRequestToGamification,
                expectedResponseFromGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(String.format(TEAM_THANKS_MESSAGE,
                convertSlackUserInSlackFormat(userFrom.getSlackUser()),
                convertSlackUserInSlackFormat(user1.getSlackUser()),
                convertSlackUserInSlackFormat(user2.getSlackUser()),
                convertSlackUserInSlackFormat(user3.getSlackUser())))
        );

        String teamCommandTextFromSlack = "";

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotTeamUrl),
                getUriVars("slashCommandToken", "/team", teamCommandTextFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnClientErrorMessageWhenUserServiceIsFail() throws Exception {
        String thanksCommandFromSlack = String.format("%s thanks %s for your help!",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()),
                convertSlackUserInFullSlackFormat(user2.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, user2, userFrom);

        String expectedResponseToSlack = "very big and scare error";

        mockFailUsersService(usersInCommand);

        mockSlackResponseUrl(responseUrl, new RichMessage(expectedResponseToSlack));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotThanksUrl),
                getUriVars("slashCommandToken", "/thanks", thanksCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    @Test
    public void returnClientErrorMessageWhenGamificationServiceIsFail() throws Exception {
        String thanksCommandFromSlack = String.format("%s thanks for your help!",
                convertSlackUserInFullSlackFormat(user1.getSlackUser()));

        List<UserDTO> usersInCommand = Arrays.asList(user1, userFrom);

        String expectedRequestToGamification = String.format("{\"description\":\"%s\",\"from\":\"%s\",\"to\":\"%s\"}",
                "thanks for your help!", userFrom.getUuid(), user1.getUuid());

        String expectedResponseToSlack = "Oops something went wrong :(";

        mockSuccessUsersService(usersInCommand);

        mockFailGamificationService(gamificationThanksUrl, expectedRequestToGamification);

        mockSlackResponseUrl(responseUrl, new RichMessage(expectedResponseToSlack));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(gamificationSlackbotThanksUrl),
                getUriVars("slashCommandToken", "/thanks", thanksCommandFromSlack))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        this.mockServer.verify();
    }

    private void mockFailUsersService(List<UserDTO> users) throws JsonProcessingException {
        List<String> slackUsers = new ArrayList<>();
        for (UserDTO user : users) {
            slackUsers.add(user.getSlackUser());
        }
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(usersFindUsersBySlackUsersUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(MockRestRequestMatchers.content().string(String.format("{\"slackIds\":%s}", mapper.writeValueAsString(slackUsers))))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
    }

    private void mockFailGamificationService(String expectedURI, String expectedRequestBody) {
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString("application/json")))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
    }

    private void mockSuccessUsersService(List<UserDTO> users) throws JsonProcessingException {
        List<String> slackUsers = new ArrayList<>();
        for (UserDTO user : users) {
            slackUsers.add(user.getSlackUser());
        }
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(usersFindUsersBySlackUsersUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(MockRestRequestMatchers.content().string(String.format("{\"slackIds\":%s}", mapper.writeValueAsString(slackUsers))))
                .andRespond(withSuccess(mapper.writeValueAsString(users), MediaType.APPLICATION_JSON_UTF8));
    }

    private void mockSuccessPostService(String expectedURI, String expectedRequestBody, String response) {
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString("application/json")))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private void mockSuccessGetService(String expectedURI, String response) {
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }

    private void mockSlackResponseUrl(String expectedURI, RichMessage delayedMessage) {
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(),
                        containsString("application/json")))
                .andExpect(request -> assertThatJson(request.getBody().toString())
                        .isEqualTo(mapper.writeValueAsString(delayedMessage)))
                .andRespond(withSuccess("OK", MediaType.APPLICATION_FORM_URLENCODED));
    }
}