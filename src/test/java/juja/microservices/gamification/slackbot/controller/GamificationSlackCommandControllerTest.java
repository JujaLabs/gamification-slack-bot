package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.exceptions.ExceptionsHandler;
import juja.microservices.gamification.slackbot.service.GamificationService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import static juja.microservices.gamification.slackbot.model.SlackParsedCommand.convertSlackUserInFullSlackFormat;
import static juja.microservices.utils.SlackUtils.getUriVars;
import static juja.microservices.utils.SlackUtils.getUrlTemplate;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 * @author Danil Kuznetsov kuznetsov.danil.v@gmail.com
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class GamificationSlackCommandControllerTest {

    private final String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";
    private final String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final String RESPONSE_URL = "http://example.com";

    private final String SLACK_USER_FROM = "UNJSD9OKM";
    private final String VALID_SLASH_COMMAND_TOKEN = "slashCommandToken";

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    private final String GAMIFICATION_SLACK_BOT_DAILY_URL = "/v1/commands/daily";
    private final String GAMIFICATION_SLACK_BOT_THANKS_URL = "/v1/commands/thanks";
    private final String GAMIFICATION_SLACK_BOT_CODENJOY_URL =  "/v1/commands/codenjoy";
    private final String GAMIFICATION_SLACK_BOT_INTERVIEW_URL = "/v1/commands/interview";
    private final String GAMIFICATION_SLACK_BOT_TEAM_URL = "/v1/commands/team";

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private ExceptionsHandler exceptionsHandler;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void onReceiveSlashCommandCodenjoyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String CODENJOY_COMMAND_TEXT = "-1th <@slack1> -2th <@slack2> -3th <@slack3>";

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_CODENJOY_URL),
                getUriVars("wrongSlackToken", "/command", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verifyNoMoreInteractions(exceptionsHandler);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {

        String codenjoyCommandText = "-1th <@slack1> -2th <@slack2> -3th <@slack3>";
        String responseToSlack = "Ok response";

        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class))).thenReturn(responseToSlack);

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_CODENJOY_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
        verify(gamificationService).sendCodenjoyAchievement(SLACK_USER_FROM, codenjoyCommandText);

        assertDelayedResponseMessage(responseToSlack);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandCodenjoyShouldReturnErrorMessageIfOccurException() throws Exception {

        String codenjoyCommandText = "-1th @slack1 -2th @slack2 with error";
        String responseToSlack = "Error response";

        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(responseToSlack));

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_CODENJOY_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/codenjoy", codenjoyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(responseToSlack);
        gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class));
    }

    @Test
    public void onReceiveSlashCommandDailyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        String dailyCommandText = "daily report";

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars("wrongSlackToken", "/command", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verifyNoMoreInteractions(exceptionsHandler);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {

        String dailyCommandText = "daily report";
        String responseToSlack = "Ok response";

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class))).thenReturn(responseToSlack);

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/daily", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
        verify(gamificationService).sendDailyAchievement(SLACK_USER_FROM, dailyCommandText);

        assertDelayedResponseMessage(responseToSlack);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandDailyShouldReturnErrorMessageIfOccurException() throws Exception {

        String dailyCommandText = "daily description text";
        String responseToSlack = "Error message";

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(responseToSlack));

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/daily", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(responseToSlack);
        gamificationService.sendDailyAchievement(any(String.class), any(String.class));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        String thanksCommandText = String.format("thanks to %s description text",
                convertSlackUserInFullSlackFormat(SLACK_USER_FROM)
        );

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_THANKS_URL),
                getUriVars("wrongSlackToken", "/command", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verifyNoMoreInteractions(exceptionsHandler);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {

        String thanksCommandText = String.format("thanks %s for help",
                convertSlackUserInFullSlackFormat(SLACK_USER_FROM));
        String responseToSlack = "Ok response";

        when(gamificationService.sendThanksAchievement(any(String.class), any(String.class))).thenReturn(responseToSlack);

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_THANKS_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
        verify(gamificationService).sendThanksAchievement(SLACK_USER_FROM, thanksCommandText);

        assertDelayedResponseMessage(responseToSlack);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandThanksWhenOccurSomeException() throws Exception {

        String thanksCommandText = "thanks @slack1 for help";
        String responseToSlack = "Error response";

        when(gamificationService.sendThanksAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(responseToSlack));

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_THANKS_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/thanks", thanksCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(responseToSlack);
        gamificationService.sendThanksAchievement(any(String.class), any(String.class));
    }

    @Test
    public void onReceiveSlashCommandInterviewWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        String interviewCommandText = "interview report";

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_INTERVIEW_URL),
                getUriVars("wrongSlashCommandToken", "/interview", interviewCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verifyNoMoreInteractions(exceptionsHandler);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        String interviewCommandText = "interview description";
        String responseToSlack = "Ok response";

        when(gamificationService.sendInterviewAchievement(any(String.class), any(String.class))).thenReturn(responseToSlack);

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_INTERVIEW_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/interview", interviewCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
        verify(gamificationService).sendInterviewAchievement(SLACK_USER_FROM, interviewCommandText);

        assertDelayedResponseMessage(responseToSlack);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandInterviewShouldReturnErrorRichMessageIfOccurException() throws Exception {
        String interviewCommandText = "interview description";
        String responseToSlack = "Error response";

        when(gamificationService.sendInterviewAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(responseToSlack));

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_INTERVIEW_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/interview", interviewCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(responseToSlack);
        gamificationService.sendInterviewAchievement(any(String.class), any(String.class));
    }

    @Test
    public void onReceiveSlashCommandTeamShouldReturnErrorRichMessageIfOccurException() throws Exception {
        String teamCommandText = "team description";
        String responseToSlack = "Error response";

        when(gamificationService.sendTeamAchievement(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException(responseToSlack));

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_TEAM_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/team", teamCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(responseToSlack);
        gamificationService.sendTeamAchievement(SLACK_USER_FROM, teamCommandText);
    }

    @Test
    public void onReceiveSlashCommandTeamWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        String teamCommandText = "team report";

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_TEAM_URL),
                getUriVars("wrongSlackToken", "/team", teamCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verifyNoMoreInteractions(exceptionsHandler);
        verifyNoMoreInteractions(gamificationService);
    }

    @Test
    public void onReceiveSlashCommandTeamReturnOkRichMessage() throws Exception {

        String teamCommandText = "team report";
        String responseToSlack = "Ok response";

        when(gamificationService.sendTeamAchievement(any(String.class), any(String.class))).thenReturn(responseToSlack);

        mvc.perform(post(getUrlTemplate(GAMIFICATION_SLACK_BOT_TEAM_URL),
                getUriVars(VALID_SLASH_COMMAND_TOKEN, "/team", teamCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        verify(exceptionsHandler).setResponseUrl(anyString());
        verify(gamificationService).sendTeamAchievement(SLACK_USER_FROM, teamCommandText);

        assertDelayedResponseMessage(responseToSlack);
        verifyNoMoreInteractions(gamificationService);
    }

    private void assertDelayedResponseMessage(String message) {
        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(RESPONSE_URL), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(message));
    }
}