package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.utils.SlackUrlUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class GamificationSlackCommandControllerTest {

    private static final String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";

    private final String FROM_USER_SLACK_NAME = "@from-user";
    private final String VALID_SLASH_COMMAND_TOKEN = "slashCommandToken";

    @Value("${gamification.slackbot.rest.api.version}")
    private String gamificationSlackbotRestApiVersion;
    @Value("${gamification.slackbot.commandsUrl}")
    private String gamificationSlackbotCommandsUrl;
    @Value("${gamification.slackbot.endpoint.daily}")
    private String gamificationSlackbotDailyUrl;
    @Value("${gamification.slackbot.endpoint.thanks}")
    private String gamificationSlackbotThanksUrl;
    @Value("${gamification.slackbot.endpoint.codenjoy}")
    private String gamificationSlackbotCodenjoyUrl;
    @Value("${gamification.slackbot.endpoint.interview}")
    private String gamificationSlackbotInterviewUrl;

    private String gamificationSlackbotFullDailyUrl;
    private String gamificationSlackbotFullThanksUrl;
    private String gamificationSlackbotFullCodenjoyUrl;
    private String gamificationSlackbotFullInterviewUrl;

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @Before
    public void setup() {
        gamificationSlackbotFullDailyUrl = "/" + gamificationSlackbotRestApiVersion + gamificationSlackbotCommandsUrl +
                gamificationSlackbotDailyUrl;
        gamificationSlackbotFullThanksUrl = "/" + gamificationSlackbotRestApiVersion + gamificationSlackbotCommandsUrl +
                gamificationSlackbotThanksUrl;
        gamificationSlackbotFullCodenjoyUrl = "/" + gamificationSlackbotRestApiVersion +
                gamificationSlackbotCommandsUrl + gamificationSlackbotCodenjoyUrl;
        gamificationSlackbotFullInterviewUrl = "/" + gamificationSlackbotRestApiVersion +
                gamificationSlackbotCommandsUrl + gamificationSlackbotInterviewUrl;
    }

    @Test
    public void onReceiveSlashCommandCodenjoyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String CODENJOY_COMMAND_TEXT = "-1th @slack1 -2th @slack2 -3th @slack3";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullCodenjoyUrl),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {

        final String CODENJOY_COMMAND_TEXT = "-1th @slack1 -2th @slack2 -3th @slack3";
        final String RESPONSE_TO_SLACK = "Ok response";

        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class))).thenReturn(RESPONSE_TO_SLACK);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullCodenjoyUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/codenjoy", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));

        verify(gamificationService).sendCodenjoyAchievement(FROM_USER_SLACK_NAME, CODENJOY_COMMAND_TEXT);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandCodenjoyShouldReturnErrorMessageIfOccurException() throws Exception {

        final String CODENJOY_COMMAND_TEXT = "-1th @slack1 -2th @slack2 with error";
        final String RESPONSE_TO_SLACK = "Error response";

        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(RESPONSE_TO_SLACK));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullCodenjoyUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/codenjoy", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));
    }

    @Test
    public void onReceiveSlashCommandDailyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String DAILY_COMMAND_TEXT = "daily report";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullDailyUrl),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {

        final String DAILY_COMMAND_TEXT = "daily report";
        final String RESPONSE_TO_SLACK = "Ok response";

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class))).thenReturn(RESPONSE_TO_SLACK);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullDailyUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));

        verify(gamificationService).sendDailyAchievement(FROM_USER_SLACK_NAME, DAILY_COMMAND_TEXT);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandDailyShouldReturnErrorMessageIfOccurException() throws Exception {

        final String DAILY_COMMAND_TEXT = "daily description text";
        final String RESPONSE_TO_SLACK = "Error message";

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(RESPONSE_TO_SLACK));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullDailyUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks to @slack_user description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullThanksUrl),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {

        final String THANKS_COMMAND_TEXT = "thanks @slack1 for help";
        final String RESPONSE_TO_SLACK = "Ok response";

        when(gamificationService.sendThanksAchievement(any(String.class), any(String.class))).thenReturn(RESPONSE_TO_SLACK);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullThanksUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));

        verify(gamificationService).sendThanksAchievement(FROM_USER_SLACK_NAME, THANKS_COMMAND_TEXT);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandThanksWhenOccurSomeException() throws Exception {

        final String THANKS_COMMAND_TEXT = "thanks @slack1 for help";
        final String RESPONSE_TO_SLACK = "Error response";

        when(gamificationService.sendThanksAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(RESPONSE_TO_SLACK));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullThanksUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));
    }

    @Test
    public void onReceiveSlashCommandInterviewWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview report";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullInterviewUrl),
                SlackUrlUtils.getUriVars("wrongSlashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text")
                        .value(SORRY_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {

        final String INTERVIEW_COMMAND_TEXT = "interview description";
        final String RESPONSE_TO_SLACK = "Ok response";

        when(gamificationService.sendInterviewAchievement(any(String.class), any(String.class))).thenReturn(RESPONSE_TO_SLACK);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullInterviewUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));

        verify(gamificationService).sendInterviewAchievement(FROM_USER_SLACK_NAME, INTERVIEW_COMMAND_TEXT);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandInterviewShouldReturnErrorRichMessageIfOccurException() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview description";
        final String RESPONSE_TO_SLACK = "Error response";

        when(gamificationService.sendInterviewAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(RESPONSE_TO_SLACK));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate(gamificationSlackbotFullInterviewUrl),
                SlackUrlUtils.getUriVars(VALID_SLASH_COMMAND_TOKEN, "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(RESPONSE_TO_SLACK));
    }
}