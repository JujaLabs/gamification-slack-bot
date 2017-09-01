package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.exceptions.ExceptionsHandler;
import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import juja.microservices.utils.SlackUrlUtils;
import me.ramswaroop.jbot.core.slack.models.RichMessage;
import org.junit.Before;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Nikolay Horushko
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GamificationSlackCommandController.class)
public class GamificationSlackCommandControllerTest {
    private final static String INSTANT_MESSAGE = "Your command accepted. Please wait...";
    private final static String SORRY_MESSAGE = "Sorry! You're not lucky enough to use our slack command.";
    private final static String CODENJOY_ERROR_MESSAGE = "ERROR. Something went wrong and we didn't award the users.";
    private final static String CODENJOY_THANKS_MESSAGE = "Thanks, we awarded the users. \" +\n" +
            "                    \"First place: %s, Second place: %s, Third place: %s.";
    private final static String DAILY_ERROR_MESSAGE = "ERROR. Something went wrong and daily report was not saved.";
    private final static String DAILY_THANKS_MESSAGE = "Thanks, your daily report saved.";
    private final static String THANKS_ERROR_MESSAGE = "Error. Something went wrong and we didn't save your 'thanks'.";
    private final static String THANKS_ONE_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved.";
    private final static String THANKS_TWO_THANKS_MESSAGE = "Thanks, your 'thanks' for %s saved. \" +\n" +
            "                    \"Also you received +1 for your activity.";
    private final static String INTERVIEW_ERROR_MESSAGE = "ERROR. Something went wrong and we didn't save your interview.";
    private final static String INTERVIEW_THANKS_MESSAGE = "Thanks. Your interview saved.";
    private final static String responseUrl = "http://example.com";

    @Rule
    public ExpectedException exceptions = ExpectedException.none();

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private SlackNameHandlerService slackNameHandlerService;

    @MockBean
    private ExceptionsHandler exceptionsHandler;

    @MockBean
    private RestTemplate restTemplate;

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;
    private UserDTO user3;

    @Before
    public void setup() {
        userFrom = new UserDTO("AAA000", "@from-user");
        user1 = new UserDTO("AAA111", "@slack1");
        user2 = new UserDTO("AAA222", "@slack2");
        user3 = new UserDTO("AAA333", "@slack3");
    }

    @Test
    public void onReceiveSlashCommandCodenjoyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String CODENJOY_COMMAND_TEXT = "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
    }

    @Test
    public void onReceiveSlashCommandCodenjoyReturnOkRichMessage() throws Exception {
        final String CODENJOY_COMMAND_TEXT = String.format("-1th %s -2th %s -3th %s",
                user1.getSlack(), user2.getSlack(), user3.getSlack());

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);
        users.put(user1.getSlack(), user1);
        users.put(user2.getSlack(), user2);
        users.put(user3.getSlack(), user3);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), CODENJOY_COMMAND_TEXT, users);
        final String[] GAMIFICATION_RESPONSE = {"1000", "1001", "1002"};

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), CODENJOY_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class))).thenReturn(GAMIFICATION_RESPONSE);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(String.format(CODENJOY_THANKS_MESSAGE,
                user1.getSlack(), user2.getSlack(), user3.getSlack())));
    }

    @Test
    public void onReceiveSlashCommandCodenjoyShouldReturnErrorMessageIfOccurException() throws Exception {
        final String CODENJOY_COMMAND_TEXT = String.format("-1th %s -2th %s -3th %s",
                user1.getSlack(), user2.getSlack(), user3.getSlack());

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);
        users.put(user1.getSlack(), user1);
        users.put(user2.getSlack(), user2);
        users.put(user3.getSlack(), user3);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), CODENJOY_COMMAND_TEXT, users);

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), CODENJOY_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class)))
                .thenThrow(new RuntimeException(CODENJOY_ERROR_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(CODENJOY_ERROR_MESSAGE);
        gamificationService.sendCodenjoyAchievement(any(CodenjoyAchievement.class));
    }

    @Test
    public void onReceiveSlashCommandDailyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String DAILY_COMMAND_TEXT = "daily description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
    }

    @Test
    public void onReceiveSlashCommandDailyReturnOkRichMessage() throws Exception {
        final String DAILY_COMMAND_TEXT = "daily description text";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), DAILY_COMMAND_TEXT, users);
        final String[] GAMIFICATION_RESPONSE = {"1000"};

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), DAILY_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendDailyAchievement(any(DailyAchievement.class))).thenReturn(GAMIFICATION_RESPONSE);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(DAILY_THANKS_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandDailyShouldReturnErrorMessageIfOccurException() throws Exception {
        final String DAILY_COMMAND_TEXT = "daily description text";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), DAILY_COMMAND_TEXT, users);

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), DAILY_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendDailyAchievement(any(DailyAchievement.class)))
                .thenThrow(new RuntimeException(DAILY_ERROR_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(DAILY_ERROR_MESSAGE);
        gamificationService.sendDailyAchievement(any(DailyAchievement.class));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks to @slack_user description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
    }

    @Test
    public void onReceiveSlashCommandThanksReturnOkRichMessage() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks @slack1 for help";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);
        users.put(user1.getSlack(), user1);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT, users);
        final String[] GAMIFICATION_RESPONSE = {"1000"};

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendThanksAchievement(any(ThanksAchievement.class))).thenReturn(GAMIFICATION_RESPONSE);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(String.format(THANKS_ONE_THANKS_MESSAGE, user1.getSlack())));
    }

    @Test
    public void onReceiveSlashCommandSecondThanksReturnOkRichMessage() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks @slack1 for help";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);
        users.put(user1.getSlack(), user1);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT, users);
        final String[] GAMIFICATION_RESPONSE = {"1000", "1001"};

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendThanksAchievement(any(ThanksAchievement.class))).thenReturn(GAMIFICATION_RESPONSE);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(String.format(THANKS_TWO_THANKS_MESSAGE, user1.getSlack())));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenOccurSomeException() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks @slack1 for help";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);
        users.put(user1.getSlack(), user1);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT, users);

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), THANKS_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendThanksAchievement(any(ThanksAchievement.class)))
                .thenThrow(new RuntimeException(THANKS_ERROR_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(THANKS_ERROR_MESSAGE);
        gamificationService.sendThanksAchievement(any(ThanksAchievement.class));
    }

    @Test
    public void onReceiveSlashCommandInterviewWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
                SlackUrlUtils.getUriVars("wrongSlashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(SORRY_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());
    }

    @Test
    public void onReceiveSlashCommandInterviewReturnOkRichMessage() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview description";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), INTERVIEW_COMMAND_TEXT, users);
        final String[] GAMIFICATION_RESPONSE = {"1000"};

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), INTERVIEW_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendInterviewAchievement(any(InterviewAchievement.class))).thenReturn(GAMIFICATION_RESPONSE);

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        ArgumentCaptor<RichMessage> captor = ArgumentCaptor.forClass(RichMessage.class);
        verify(restTemplate).postForObject(eq(responseUrl), captor.capture(), eq(String.class));
        assertTrue(captor.getValue().getText().contains(INTERVIEW_THANKS_MESSAGE));
    }

    @Test
    public void onReceiveSlashCommandInterviewShouldReturnErrorRichMessageIfOccurException() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview description";

        Map<String, UserDTO> users = new HashMap<>();
        users.put(userFrom.getSlack(), userFrom);

        SlackParsedCommand slackParsedCommand = new SlackParsedCommand(userFrom.getSlack(), INTERVIEW_COMMAND_TEXT, users);

        when(slackNameHandlerService.createSlackParsedCommand(userFrom.getSlack(), INTERVIEW_COMMAND_TEXT))
                .thenReturn(slackParsedCommand);
        when(gamificationService.sendInterviewAchievement(any(InterviewAchievement.class)))
                .thenThrow(new RuntimeException(INTERVIEW_ERROR_MESSAGE));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(INSTANT_MESSAGE));
        verify(exceptionsHandler).setResponseUrl(anyString());

        exceptions.expect(RuntimeException.class);
        exceptions.expectMessage(INTERVIEW_ERROR_MESSAGE);
        gamificationService.sendInterviewAchievement(any(InterviewAchievement.class));
    }
}