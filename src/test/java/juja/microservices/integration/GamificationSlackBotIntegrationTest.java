package juja.microservices.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import juja.microservices.gamification.slackbot.GamificationSlackBotApplication;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.utils.SlackUrlUtils;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GamificationSlackBotApplication.class})
@AutoConfigureMockMvc
public class GamificationSlackBotIntegrationTest {

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

    private final List<UserDTO> USERS = new ArrayList<>();

    @Before
    public void setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();

        USERS.add(new UserDTO("f2034f22-562b-4e02-bfcf-ec615c1ba62b", "@slack1"));
        USERS.add(new UserDTO("f2034f33-563c-4e03-bfcf-ec615c1ba63c", "@slack2"));
        USERS.add(new UserDTO("f2034f44-563d-4e04-bfcf-ec615c1ba64d", "@slack3"));
        USERS.add(new UserDTO("f2034f11-561a-4e01-bfcf-ec615c1ba61a", "@from-user"));
    }

//    @Test
//    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
//        final String CODENJOY_COMMAND_FROM_SLACK = "-1th @slack1 -2th @slack2 -3th @slack3";
//        mockSuccessUsersService(USERS);
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
//                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}",
//                USERS.get(0).getUuid(), USERS.get(1).getUuid(), USERS.get(2).getUuid(), USERS.get(3).getUuid());
//
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\", \"102\", \"103\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendCodenjoy, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, we awarded the users.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }

//    @Test
//    public void returnOkMessageIfCondejoyCommandTokensWithoutSpaces() throws Exception {
//        final String CODENJOY_COMMAND_FROM_SLACK = "-1th@slack1 -2th@slack2 -3th @slack3";
//        mockSuccessUsersService(USERS.get(0), USERS.get(1), USERS.get(2), USERS.get(3));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
//                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}",
//                USERS.get(0).getUuid(), USERS.get(1).getUuid(), USERS.get(2).getUuid(), USERS.get(3).getUuid());
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\", \"102\", \"103\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendCodenjoy, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, we awarded the users.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void returnOkMessageIfCondejoyCommandTokensInWrongOrder() throws Exception {
//        final String CODENJOY_COMMAND_FROM_SLACK = "-2th @slack2 -1th @slack1 -3th @slack3";
//        mockSuccessUsersService(USERS.get(0), USERS.get(2), USERS.get(1), USERS.get(3));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"firstPlace\":\"%s\"," +
//                        "\"secondPlace\":\"%s\",\"thirdPlace\":\"%s\"}",
//                USERS.get(0).getUuid(), USERS.get(1).getUuid(), USERS.get(2).getUuid(), USERS.get(3).getUuid());
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\", \"102\", \"103\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendCodenjoy, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, we awarded the users.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void returnErrorMessageIfCondejoyCommandWithout2thToken() throws Exception {
//        final String CODENJOY_COMMAND_FROM_SLACK = "-1th @slack1 @slack2 -3th @slack3";
//        mockSuccessUsersService(USERS.get(0), USERS.get(1), USERS.get(2), USERS.get(3));
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "token '-2th' not found. Example for this command \"" +
//                "/codenjoy -1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3\"";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
//        final String DAILY_COMMAND_TEXT_FROM_SLACK = "I did smth today";
//        mockSuccessUsersService(USERS.get(0));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"description\":\"%s\"}",
//                USERS.get(0).getUuid(), DAILY_COMMAND_TEXT_FROM_SLACK);
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendDaily, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, your daily report saved.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", DAILY_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
//        final String INTERVIEW_COMMAND_TEXT_FROM_SLACK = "I went to an interview yesterday and got offer";
//        mockSuccessUsersService(USERS.get(0));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"description\":\"%s\"}",
//                USERS.get(0).getUuid(), INTERVIEW_COMMAND_TEXT_FROM_SLACK);
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendInterview, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks. Your interview saved.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
//        final String THANKS_COMMAND_TEXT_FROM_SLACK = "@slack1 thanks for your help!";
//        mockSuccessUsersService(USERS.get(0), USERS.get(1));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"to\":\"%s\",\"description\":\"%s\"}",
//                USERS.get(0).getUuid(), USERS.get(1).getUuid(), " thanks for your help!");
//        final String EXPECTED_RESPONSE_FROM_GAMIFICATION = "[\"101\"]";
//
//        mockSuccessGamificationService(urlBaseGamification + urlSendThanks, EXPECTED_REQUEST_TO_GAMIFICATION,
//                EXPECTED_RESPONSE_FROM_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Thanks, your 'thanks' saved.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//    @Test
//    public void returnErrorMessageIfThanksCommandConsistTwoOrMoreSlackNames() throws Exception {
//        final String THANKS_COMMAND_TEXT_FROM_SLACK = "@slack1 thanks @slack2 for your help!";
//        mockSuccessUsersService(USERS.get(0), USERS.get(1), USERS.get(2));
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Wrong command. Example for this command " +
//                "/thanks Thanks to @slack_nick_name for help.";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//
//
//    @Test
//    public void returnClientErrorMessageWhenUserServiceIsFail() throws Exception {
//        final String THANKS_COMMAND_TEXT_FROM_SLACK = "@slack1 thanks @slack2 for your help!";
//        mockFailUsersService(USERS.get(0), USERS.get(1), USERS.get(2));
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Oops something went wrong :(";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }
//
//
//    @Test
//    public void returnClientErrorMessageWhenGamificationServiceIsFail() throws Exception {
//        final String THANKS_COMMAND_TEXT_FROM_SLACK = "@slack1 thanks for your help!";
//        mockSuccessUsersService(USERS.get(0), USERS.get(1));
//
//        final String EXPECTED_REQUEST_TO_GAMIFICATION = String.format("{\"from\":\"%s\",\"to\":\"%s\",\"description\":\"%s\"}",
//                USERS.get(0).getUuid(), USERS.get(1).getUuid(), " thanks for your help!");
//
//        mockFailGamificationService(urlBaseGamification + urlSendThanks, EXPECTED_REQUEST_TO_GAMIFICATION);
//
//        final String EXPECTED_RESPONSE_TO_SLACK = "Oops something went wrong :(";
//
//        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
//                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT_FROM_SLACK))
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.text").value(EXPECTED_RESPONSE_TO_SLACK));
//    }


    private void mockFailUsersService(UserDTO... users) {
        for (UserDTO user : users) {
            mockServer.expect(requestTo(urlBaseUser + urlGetUser))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(String.format("{\"slackNames\":[\"%s\"]}", user.getSlack())))
                    .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                            "\"clientMessage\":\"Oops something went wrong :(\"," +
                            "\"developerMessage\":\"General exception for this service\"," +
                            "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));
        }
    }


    private void mockFailGamificationService(String expectedURI, String expectedRequestBody) {
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString("application/json")))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withBadRequest().body("{\"httpStatus\":400,\"internalErrorCode\":1," +
                        "\"clientMessage\":\"Oops something went wrong :(\"," +
                        "\"developerMessage\":\"General exception for this service\"," +
                        "\"exceptionMessage\":\"very big and scare error\",\"detailErrors\":[]}"));

    }

    private void mockSuccessUsersService(List<UserDTO> users) throws JsonProcessingException {
        String expectedSlackNameRequest = new String();
        for (int i = 0; i < users.size(); i++) {
            if (i == 0) {
                expectedSlackNameRequest = expectedSlackNameRequest + String.format("\"%s\"", users.get(i).getSlack());
            } else {
                expectedSlackNameRequest = expectedSlackNameRequest + String.format(",\"%s\"", users.get(i).getSlack());
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mockServer.expect(requestTo(urlBaseUser + urlGetUser))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(String.format("{\"slackNames\":[%s]}", expectedSlackNameRequest)))
                .andRespond(withSuccess(mapper.writeValueAsString(users), MediaType.APPLICATION_JSON_UTF8));
    }

    private void mockSuccessGamificationService(String expectedURI, String expectedRequestBody, String response) {
        mockServer.expect(requestTo(expectedURI))
                .andExpect(method(HttpMethod.POST))
                .andExpect(request -> assertThat(request.getHeaders().getContentType().toString(), containsString("application/json")))
                .andExpect(request -> assertThat(request.getBody().toString(), equalTo(expectedRequestBody)))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

    }
}
