package juja.microservices.gamification.slackbot.controller;

import juja.microservices.gamification.slackbot.model.DTO.UserDTO;
import juja.microservices.gamification.slackbot.model.SlackParsedCommand;
import juja.microservices.gamification.slackbot.model.achievements.CodenjoyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.DailyAchievement;
import juja.microservices.gamification.slackbot.model.achievements.InterviewAchievement;
import juja.microservices.gamification.slackbot.model.achievements.ThanksAchievement;
import juja.microservices.gamification.slackbot.service.GamificationService;
import juja.microservices.gamification.slackbot.service.impl.SlackNameHandlerService;
import juja.microservices.utils.SlackUrlUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
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

    @Inject
    private MockMvc mvc;

    @MockBean
    private GamificationService gamificationService;

    @MockBean
    private SlackNameHandlerService slackNameHandlerService;

    private UserDTO userFrom;
    private UserDTO user1;
    private UserDTO user2;
    private UserDTO user3;

    @Before
    public void setup() {
        userFrom = new UserDTO("AAA000","@from-user" );
        user1 = new UserDTO("AAA111","@slack1" );
        user2 = new UserDTO("AAA222","@slack2" );
        user3 = new UserDTO("AAA333","@slack3");
    }

    @Test
    public void onReceiveSlashCommandCodenjoyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String CODENJOY_COMMAND_TEXT = "-1th @slack_nick_name -2th @slack_nick_name2 -3th @slack_nick_name3";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
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
                .andExpect(jsonPath("$.text").value("Thanks, we awarded the users."));
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
                .thenThrow(new RuntimeException("something went wrong"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/codenjoy"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/codenjoy", CODENJOY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }

    @Test
    public void onReceiveSlashCommandDailyWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String DAILY_COMMAND_TEXT = "daily description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
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
                .andExpect(jsonPath("$.text").value("Thanks, your daily report saved."));
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
                .thenThrow(new RuntimeException("something went wrong"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/daily"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/daily", DAILY_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }

    @Test
    public void onReceiveSlashCommandThanksWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String THANKS_COMMAND_TEXT = "thanks to @slack_user description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("wrongSlackToken", "/command", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(SORRY_MESSAGE));
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
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Thanks, your 'thanks' saved."));
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
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Thanks, your 'thanks' saved. " +
                        "Also you received +1 for your activity."));
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
                .thenThrow(new RuntimeException("something went wrong"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/thanks"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/thanks", THANKS_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }

    @Test
    public void onReceiveSlashCommandInterviewWhenIncorrectTokenShouldReturnSorryRichMessage() throws Exception {
        final String INTERVIEW_COMMAND_TEXT = "interview description text";

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
                SlackUrlUtils.getUriVars("wrongSlashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text")
                        .value("Sorry! You're not lucky enough to use our slack command."));
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
                .andExpect(jsonPath("$.text").value("Thanks. Your interview saved."));
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
                .thenThrow(new RuntimeException("something went wrong"));

        mvc.perform(MockMvcRequestBuilders.post(SlackUrlUtils.getUrlTemplate("/commands/interview"),
                SlackUrlUtils.getUriVars("slashCommandToken", "/interview", INTERVIEW_COMMAND_TEXT))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("something went wrong"));
    }
}