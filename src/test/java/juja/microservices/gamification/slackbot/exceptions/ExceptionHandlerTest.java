package juja.microservices.gamification.slackbot.exceptions;

import juja.microservices.gamification.slackbot.controller.GamificationSlackCommandController;
import juja.microservices.gamification.slackbot.service.GamificationService;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.Collections;

import static juja.microservices.utils.SlackUtils.getUriVars;
import static juja.microservices.utils.SlackUtils.getUrlTemplate;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Danil Kuznetsov
 * @author Nikolay Horushko
 * @author Petr Kramar
 * @author Ivan Shapovalov
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class ExceptionHandlerTest {
    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String responseUrl = "http://example.com";

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private RestTemplate restTemplate;

    private final String GAMIFICATION_SLACK_BOT_DAILY_URL = "/v1/commands/daily";
    private final String GAMIFICATION_SLACK_BOT_CODENJOY_URL =  "/v1/commands/codenjoy";
    private final String GAMIFICATION_SLACK_BOT_TEAM_URL = "/v1/commands/team";
    private final String GAMIFICATION_SLACK_BOT_INTERVIEW_URL = "/v1/commands/interview";
    private final String GAMIFICATION_SLACK_BOT_THANKS_URL = "/v1/commands/thanks";

    @Test
    public void shouldHandleGamificationAPIError() throws Exception {

        String dailyCommandText = "daily description text";

        ApiError apiError = new ApiError(
                400, "GMF-F5-D2",
                "You cannot give more than one thanks for day to one person",
                "The reason of the exception is 'Thanks' achievement",
                "Something went wrong",
                Collections.emptyList()
        );

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class)))
                .thenThrow(new GamificationExchangeException(apiError, new RuntimeException("exception")));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars("slashCommandToken", "/daily", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("You cannot give more than one thanks for day to one person"));
    }

    @Test
    public void shouldHandleUserAPIError() throws Exception {

        String dailyCommandText = "daily description text";

        ApiError apiError = new ApiError(
                400, "USF-F1-D1",
                "User not found",
                "User not found",
                "Something went wrong",
                Collections.emptyList()
        );

        when(gamificationService.sendDailyAchievement(any(), any())).
                thenThrow(new UserExchangeException(apiError, new RuntimeException("exception")));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars("slashCommandToken", "/daily", dailyCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("Something went wrong"));
    }

    @Test
    public void shouldHandleTeamAPIError() throws Exception {

        String teamCommandText = "";

        ApiError apiError = new ApiError(
                400, "TMF-F4-D1",
                "Team not found",
                "Team not found",
                "Something went wrong",
                Collections.emptyList()
        );

        when(gamificationService.sendTeamAchievement(any(), any())).
                thenThrow(new TeamExchangeException(apiError, new RuntimeException("exception")));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_TEAM_URL),
                getUriVars("slashCommandToken", "/team", teamCommandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("Team not found"));
    }

    @Test
    public void shouldHandleResourceAccessException() throws Exception {
        String commandText = "@slack1 -2th @slack2 -3th @slack3";
        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class))).
                thenThrow(new ResourceAccessException("Some service unavailable"));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_CODENJOY_URL),
                getUriVars("slashCommandToken", "/codenjoy", commandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("Some service unavailable"));
        verify(gamificationService).sendCodenjoyAchievement(any(String.class), any(String.class));
        verifyNoMoreInteractions(gamificationService, restTemplate);
    }

    @Test
    public void shouldHandleWrongCommandException() throws Exception {

        String commandText = "@slack1 -2th @slack2 -3th @slack3";

        when(gamificationService.sendCodenjoyAchievement(any(String.class), any(String.class))).
                thenThrow(new WrongCommandFormatException("Wrong command exception"));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_CODENJOY_URL),
                getUriVars("slashCommandToken", "/codenjoy", commandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("Wrong command exception"));
    }

    @Test
    public void shouldHandleAllOtherException() throws Exception {

        String commandText = "daily report";

        when(gamificationService.sendDailyAchievement(any(String.class), any(String.class))).
                thenThrow(new RuntimeException("Runtime exception"));

        mvc.perform(MockMvcRequestBuilders.post(getUrlTemplate(GAMIFICATION_SLACK_BOT_DAILY_URL),
                getUriVars("slashCommandToken", "/daily", commandText))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains("Runtime exception"));
    }
}